import styled from "styled-components";
import { GlobalStyle } from "../../action/GlobalStyle";

function PlanTime(props) {
  const month = props.date.toLocaleString("en-US", { month: "2-digit" });
  const day = props.date.toLocaleString("en-US", { day: "2-digit" });
  const year = props.date.getFullYear().toString().slice(2);

  const date = [
    {
      type: "YEAR",
      value: year,
    },
    {
      type: "MONTH",
      value: month,
    },
    {
      type: "DAY",
      value: day,
    },
  ];

  const dday = date.map((elem) => {
    return (
      <Layout>
        <DayTitle>{elem.type}</DayTitle>
        <Text>{elem.value}</Text>
      </Layout>
    );
  });

  return (
    <TimeLayout>
      <GlobalStyle />
      {dday}
    </TimeLayout>
  );
}

export default PlanTime;

const DayTitle = styled.div`
  font-weight: 600;
`;

const Text = styled.div`
  width: 2em;
  height: 2em;
  font-weight: bolder;
  font-size: 2rem;
  padding: 1em;
  border-radius: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
  grid-column: 1 / 2;
  grid-row: 4 / 5;
  background-color: var(--primary);
  box-shadow: inset 0.2rem 0.2rem 1rem var(--primary-light),
    inset -0.2rem -0.2rem 1rem var(--primary-dark),
    0.3rem 0.3rem 0.6rem var(--greyLight-2), -0.2rem -0.2rem 0.5rem var(--white);
  color: var(--greyLight-1);
`;

const Layout = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
`;

const TimeLayout = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-bottom: 4rem;
`;
