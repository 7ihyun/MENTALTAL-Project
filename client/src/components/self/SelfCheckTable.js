import styled from "styled-components";

const SelfCheckTable = ({ title, data, type }) => {
    return (
        <SCTableWrapper>
            <SCTableTitleWrapper>
                <div>{title}</div>
                <div>아래에 적혀 있는 문항을 읽으신 후, 자신이 느끼고 행동한 것을 잘 나타낸다고 생각하는 항목에 체크하시면 됩니다.</div>
                <div>입력하신 정보는 본 검사 외 다른 용도로 사용되지 않으며 저장되지 않습니다.</div>
            </SCTableTitleWrapper>

            <SCTableMainWrapper>
                <SCTableMainHedaer>
                    <tr>
                        <th>번호</th>
                        <th>문항</th>
                        {type.map((el, idx) => {
                            return <th key={idx}>{el}</th>;
                        })}
                    </tr>
                </SCTableMainHedaer>

                <SCTableMainBody>
                    {data.map((el, idx) => {
                        return (
                            <tr key={idx}>
                                <th>{idx + 1}</th>
                                <th>{el}</th>
                                <th>
                                    <input type="radio" name={`${idx}`} value={0} className="radioBtn" />
                                </th>
                                <th>
                                    <input type="radio" name={`${idx}`} value={1} className="radioBtn" />
                                </th>
                                <th>
                                    <input type="radio" name={`${idx}`} value={2} className="radioBtn" />
                                </th>
                                <th>
                                    <input type="radio" name={`${idx}`} value={3} className="radioBtn" />
                                </th>
                            </tr>
                        );
                    })}
                </SCTableMainBody>
            </SCTableMainWrapper>
        </SCTableWrapper>
    );
};

const SCTableWrapper = styled.div`
    width: 100%;
    padding: 40px;

    background-color: white;
    border-radius: 10px;
`;

const SCTableTitleWrapper = styled.div`
    width: 100%;

    display: flex;
    flex-direction: column;

    gap: 5px;

    & :nth-child(1) {
        color: var(--darkgreen);
        font-size: 30px;
        font-weight: 600;
        margin-bottom: 10px;
    }

    & :not(:nth-child(1)) {
        color: var(--green);
    }
`;

const SCTableMainWrapper = styled.table`
    width: 100%;
    margin: 30px 0;

    color: var(--darkgreen);
`;

const SCTableMainHedaer = styled.thead`
    border-bottom: 1px solid var(--darkgreen);
    font-size: 14px;
    font-weight: 600;

    tr {
        padding: 15px 0;
        display: grid;
        grid-template-columns: 1fr 6fr 1fr 1fr 1fr 1fr;
        justify-items: center;
        align-items: center;
    }
`;

const SCTableMainBody = styled.tbody`
    tr {
        padding: 15px 0;
        display: grid;
        grid-template-columns: 1fr 6fr 1fr 1fr 1fr 1fr;
        align-items: center;
    }

    tr :nth-child(2) {
        text-align: left;
    }
`;

export default SelfCheckTable;
