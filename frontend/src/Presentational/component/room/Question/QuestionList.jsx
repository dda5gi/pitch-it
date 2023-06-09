import React, { memo } from "react";
import styled from "styled-components";
import QuestionItem from "./QuestionItem";

//질문 목록 받아온 것 mapping 해서 보여줄 수 있도록
const QuestionList = ({ Questions, userinfo, pdfhandler }) => {
  return (
    <List>
      {Questions.map((Question) => (
        <QuestionItem
          Question={Question}
          key={Question.id}
          userinfo={userinfo}
          pdfhandler={pdfhandler}
        />
      ))}
    </List>
  );
};

export default memo(QuestionList);

const List = styled.div`
  height: 100%;
  width: inherit;
  overflow-y: scroll;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    width: 7px;
    border-radius: 1rem;
  }

  &::-webkit-scrollbar-thumb {
    background-color: var(--greyLight-2);
    border-radius: 1rem;
  }

  &::-webkit-scrollbar-track {
    background-color: var(--greyLight-1);
  }
`;
