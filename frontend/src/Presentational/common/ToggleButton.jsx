import styled from "styled-components";

const ToggleButton = ({ toggle, ToggleHandler }) => {
  const ClickedToggle = () => {
    ToggleHandler(!toggle);
  };

  return (
    <SwitchBox>
      <div>
        <input id="switch-1" type="checkbox" />
        <label htmlFor="switch-1" onClick={ClickedToggle}></label>
      </div>
    </SwitchBox>
  );
};

const SwitchBox = styled.div`
  grid-column: 1 / 2;
  display: grid;
  grid-template-columns: repeat(2, min-content);
  grid-gap: 3rem;
  justify-self: center;

  input {
    display: none;
  }

  div {
    width: 6rem;
    
    label {
      display: flex;
      align-items: center;
      width: 4rem;
      height: 2rem;
      box-shadow: .3rem .3rem .6rem var(--greyLight-2), -.2rem -.2rem .5rem var(--white);
      background: rgba(255, 255, 255, 0);
      position: relative;
      cursor: pointer;
      border-radius: 1.6rem;

      &::after {
        content: "";
        position: absolute;
        left: 0.4rem;
        width: 1.2rem;
        height: 1.2rem;
        border-radius: 50%;
        background: var(--greyDark);
        transition: all 0.4s ease;
      }
      &::before {
        content: "";
        width: 100%;
        height: 100%;
        border-radius: inherit;
        background: linear-gradient(
          330deg,
          var(--primary-dark) 0%,
          var(--primary) 50%,
          var(--primary-light) 100%
        );
        opacity: 0;
        transition: all 0.4s ease;
      }
    }
  }

  & input:checked {
    & ~ label {
      &::before {
        opacity: 1;
      }
      &::after {
        left: 63%;
        background: var(--greyLight-1);
      }
    }
  }
`;


//#region DarkModToggleButton
const DarkModToggleButton = ({ handler }) => {
  return (
    <ToggleWrapper>
      <DN className="dn" id="dn" />
      <Toggle className="toggle" onClick={handler} htmlFor="dn">
        <ToggleHandler className="toggle__handler">
          <Crater className="crater crater--1"></Crater>
          <Crater className="crater crater--2"></Crater>
          <Crater className="crater crater--3"></Crater>
        </ToggleHandler>
        <Star className="star star--1"></Star>
        <Star className="star star--2"></Star>
        <Star className="star star--3"></Star>
        <Star className="star star--4"></Star>
        <Star className="star star--5"></Star>
        <Star className="star star--6"></Star>
      </Toggle>
    </ToggleWrapper>
  );
};
//#region DarkMod Styled-Component
const ToggleWrapper = styled.div`
  & .dn {
    position: absolute;
    left: -99em;
    &:checked {
      + .toggle {
        background-color: #749dd6;

        .toggle__handler {
          background-color: #ffe5b5;
          transform: translate3d(40px, 0, 0) rotate(0);

          .crater {
            opacity: 1;
          }
        }

        .star--1 {
          width: 2px;
          height: 2px;
        }

        .star--2 {
          width: 4px;
          height: 4px;
          transform: translate3d(-5px, 0, 0);
        }

        .star--3 {
          width: 2px;
          height: 2px;
          transform: translate3d(-7px, 0, 0);
        }

        .star--4,
        .star--5,
        .star--6 {
          opacity: 1;
          transform: translate3d(0, 0, 0);
        }
        .star--4 {
          transition: all 300ms 200ms cubic-bezier(0.445, 0.05, 0.55, 0.95);
        }
        .star--5 {
          transition: all 300ms 300ms cubic-bezier(0.445, 0.05, 0.55, 0.95);
        }
        .star--6 {
          transition: all 300ms 400ms cubic-bezier(0.445, 0.05, 0.55, 0.95);
        }
      }
    }
  }
`;

const DN = styled.input.attrs({ type: "checkbox" })``;

const Toggle = styled.label`
  cursor: pointer;
  display: inline-block;
  position: relative;
  width: 90px;
  height: 50px;
  background-color: #83d8ff;
  border-radius: 84px;
  transition: background-color 200ms cubic-bezier(0.445, 0.05, 0.55, 0.95);

  .star {
    position: absolute;
    background-color: #ffffff;
    transition: all 300ms cubic-bezier(0.445, 0.05, 0.55, 0.95);
    border-radius: 50%;
  }

  .star--1 {
    top: 10px;
    left: 35px;
    z-index: 1;
    width: 30px;
    height: 3px;
  }

  .star--2 {
    top: 18px;
    left: 28px;
    z-index: 1;
    width: 30px;
    height: 3px;
  }

  .star--3 {
    top: 27px;
    left: 40px;
    z-index: 0;
    width: 30px;
    height: 3px;
  }

  .star--4,
  .star--5,
  .star--6 {
    opacity: 0;
    transition: all 300ms 0 cubic-bezier(0.445, 0.05, 0.55, 0.95);
  }

  .star--4 {
    top: 16px;
    left: 11px;
    z-index: 0;
    width: 2px;
    height: 2px;
    transform: translate3d(3px, 0, 0);
  }

  .star--5 {
    top: 32px;
    left: 17px;
    z-index: 0;
    width: 3px;
    height: 3px;
    transform: translate3d(3px, 0, 0);
  }

  .star--6 {
    top: 36px;
    left: 28px;
    z-index: 0;
    width: 2px;
    height: 2px;
    transform: translate3d(3px, 0, 0);
  }
`;

const ToggleHandler = styled.span`
  display: inline-block;
  position: relative;
  z-index: 1;
  top: 3px;
  left: 3px;
  width: 44px;
  height: 44px;
  background-color: #ffcf96;
  border-radius: 50px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  transition: all 400ms cubic-bezier(0.68, -0.55, 0.265, 1.55);
  transform: rotate(-45deg);

  .crater {
    position: absolute;
    background-color: #e8cda5;
    opacity: 0;
    transition: opacity 200ms ease-in-out;
    border-radius: 100%;
  }

  .crater--1 {
    top: 18px;
    left: 10px;
    width: 4px;
    height: 4px;
  }

  .crater--2 {
    top: 28px;
    left: 22px;
    width: 6px;
    height: 6px;
  }

  .crater--3 {
    top: 10px;
    left: 25px;
    width: 8px;
    height: 8px;
  }
`;

const Crater = styled.span``;
const Star = styled.span``;

export { ToggleButton, DarkModToggleButton };
