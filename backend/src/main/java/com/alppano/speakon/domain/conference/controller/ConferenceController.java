package com.alppano.speakon.domain.conference.controller;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.alppano.speakon.common.dto.ApiResponse;
import com.alppano.speakon.common.exception.ResourceForbiddenException;
import com.alppano.speakon.common.exception.ResourceNotFoundException;
import com.alppano.speakon.domain.conference.dto.Conference;
import com.alppano.speakon.domain.conference.dto.InterviewRequest;
import com.alppano.speakon.domain.conference.service.ConferenceService;
import com.alppano.speakon.domain.conference.service.HttpRequestService;
import com.alppano.speakon.domain.interview_room.dto.InterviewRoomDetailInfo;
import com.alppano.speakon.domain.interview_room.service.InterviewRoomService;
import com.alppano.speakon.security.LoginUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

@Tag(name = "화상회의 관리")
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/conference")
@Slf4j
public class ConferenceController {

    @Value("${openvidu.OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${openvidu.OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    private final HttpRequestService httpRequestService;
    private final ConferenceService conferenceService;
    private final InterviewRoomService interviewRoomService;

    @PostConstruct
    public void init() { // OPENVIDU 초기화
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     OpenVidu에 세션 등록 + Redis에 세션 등록
     */
    @Operation(summary = "화상회의 세션 생성")
    @PostMapping("/sessions/{interviewRoomId}")
    public ResponseEntity<ApiResponse> initializeSession(@PathVariable("interviewRoomId") Long interviewRoomId,
                                                    @AuthenticationPrincipal LoginUser loginUser)
            throws OpenViduJavaClientException, OpenViduHttpException, JsonProcessingException {
        Map<String, Object> openViduParams = null; // OpenVidu 세션 설정이 가능함
        SessionProperties properties = SessionProperties.fromJson(openViduParams).build();

        Long requesterId = loginUser.getId();
        log.info("세션 생성 요청자 ID: {}", requesterId);
        log.info("면접방 ID: {}", interviewRoomId);

        InterviewRoomDetailInfo interviewRoomDetailInfo = interviewRoomService.getInterviewRoomDetailInfo(interviewRoomId, requesterId);
        if(!interviewRoomDetailInfo.getManager().getId().equals(requesterId)) {
            throw new ResourceForbiddenException("방을 생성할 권한이 없습니다.");
        }

        Session session = openvidu.createSession(properties);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // OpenVidu 세션이 만들어지면 Redis에 회의 정보 등록
        conferenceService.createConference(session.getSessionId(), interviewRoomDetailInfo);

        ApiResponse result = new ApiResponse(Boolean.TRUE, "세션 생성 성공");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "화상회의 세션 종료")
    @DeleteMapping("/sessions/close/{interviewRoomId}")
    public ResponseEntity<ApiResponse> closeConference(@PathVariable("interviewRoomId") Long interviewRoomId,
                                                    @AuthenticationPrincipal LoginUser loginUser)
            throws OpenViduJavaClientException, OpenViduHttpException, JsonProcessingException {
        Long requesterId = loginUser.getId();
        log.info("세션 종료 요청자 ID: {}", requesterId);
        log.info("면접방 ID: {}", interviewRoomId);

        Conference conference = conferenceService.retrieveConference(interviewRoomId);

        if(!requesterId.equals(conference.getManagerId())) {
            throw new ResourceForbiddenException("방을 종료할 권한이 없습니다.");
        }

        conferenceService.deleteConference(interviewRoomId);
        Session session = openvidu.getActiveSession(conference.getSessionId());
        session.close();
        ApiResponse result = new ApiResponse(Boolean.TRUE, "세션 종료 성공");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "화상회의 연결(토큰 발급)")
    @GetMapping("/sessions/connections/{sessionId}")
    public ResponseEntity<ApiResponse<String>> createConnection(@PathVariable("sessionId") String sessionId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            throw new ResourceNotFoundException("존재하지 않는 세션입니다.");
        }

        Map<String, Object> openViduParams = null; // OpenVidu 세션 설정이 가능함
        ConnectionProperties properties = ConnectionProperties.fromJson(openViduParams).build();
        Connection connection = session.createConnection(properties);

        ApiResponse<String> result = new ApiResponse(Boolean.TRUE, "토큰 발급 성공", connection.getToken());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "면접자 지정(인터뷰 시작)")
    @PostMapping("/interview/interviewee")
    public ResponseEntity<String> selectInterviewee(@RequestBody InterviewRequest requestDto,
                                                    @AuthenticationPrincipal LoginUser loginUser) throws Exception {
        //TODO: 검사 - 요청자가 방장인가
        //TODO: 검사 - 지정한 면접자가 참여자인지
        Conference conference = conferenceService.retrieveConference(requestDto.getInterviewRoomId());
        String intervieweeId = String.valueOf(requestDto.getIntervieweeId());

        HttpResponse response =	httpRequestService.broadCastSignal(conference.getSessionId(), "broadcast-interviewee", intervieweeId);
        StatusLine sl = response.getStatusLine();
        System.out.print("STATUS CODE: ");
        System.out.println(sl.getStatusCode());
        //TODO: response 처리 200-성공, 404-존재하지 않는 세션, 400-파라미터 오류, 406-수신자 오류(참여자 없음or유효하지 않은 커넥션ID)

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "질문 제안")
    @PostMapping("/interview/question/propose")
    public ResponseEntity<String> proposeQuestion(@RequestBody InterviewRequest requestDto,
                                                  @AuthenticationPrincipal LoginUser loginUser) throws Exception {
        Conference conference = conferenceService.retrieveConference(requestDto.getInterviewRoomId());
        String intervieweeId = String.valueOf(requestDto.getIntervieweeId());
        String questionId = String.valueOf(requestDto.getQuestionId());
        //TODO: 이미 질문이 '시작'되어 진행 중인지 검사 -> 진행 중이면 요청 거절
        //TODO: 질문 시작되었음을 기록

        HttpResponse response = httpRequestService.broadCastSignal(conference.getSessionId(), "broadcast-question-start", questionId);
        StatusLine sl = response.getStatusLine();
        System.out.print("STATUS CODE: ");
        System.out.println(sl.getStatusCode());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "질문 종료")
    @PostMapping("/interview/question/end")
    public ResponseEntity<String> endQuestion(@RequestBody InterviewRequest requestDto,
                                              @AuthenticationPrincipal LoginUser loginUser) throws Exception {
        Conference conference = conferenceService.retrieveConference(requestDto.getInterviewRoomId());
        String intervieweeId = String.valueOf(requestDto.getIntervieweeId());
        String questionId = String.valueOf(requestDto.getQuestionId());
        //TODO: 이미 질문이 '시작'되어 진행 중인지 검사 -> 질문이 진행 중이지 않은 상태면 요청 거절
        //TODO: 진행중인 질문자와 동일인인지 검사
        //TODO: 질문이 종료 되었음을 기록

        HttpResponse response = httpRequestService.broadCastSignal(conference.getSessionId(), "broadcast-question-end", questionId);
        StatusLine sl = response.getStatusLine();
        System.out.print("STATUS CODE: ");
        System.out.println(sl.getStatusCode());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 현재 진행 중인 면접자 XXX의 '인터뷰' 종료 요청
    @Operation(summary = "인터뷰 종료")
    @PostMapping("/interview/end")
    public ResponseEntity<String> endInterview(@RequestBody InterviewRequest requestDto,
                                               @AuthenticationPrincipal LoginUser loginUser) throws Exception {
        Conference conference = conferenceService.retrieveConference(requestDto.getInterviewRoomId());
        String intervieweeId = String.valueOf(requestDto.getIntervieweeId());
        String questionId = String.valueOf(requestDto.getQuestionId());
        //TODO: 검사 - 현재 진행 중인 면접자인가
        //TODO: 검사 - 요청자가 방장인가

        HttpResponse response = httpRequestService.broadCastSignal(conference.getSessionId(), "broadcast-interview-end", intervieweeId);
        StatusLine sl = response.getStatusLine();
        System.out.print("STATUS CODE: ");
        System.out.println(sl.getStatusCode());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
