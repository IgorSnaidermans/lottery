package lv.igors.lottery.statusResponse;


import org.springframework.stereotype.Component;

@Component
public class StatusResponseManager {
    public StatusResponse buildOkWithLotteryId(Long lotteryId) {
        return StatusResponse.builder()
                .id(lotteryId)
                .status("OK")
                .build();
    }

    public StatusResponse buildFailWithMessage(String message) {
        return StatusResponse.builder()
                .status("Fail")
                .reason(message)
                .build();
    }

    public StatusResponse buildOk() {
        return StatusResponse.builder()
                .status("OK")
                .build();
    }

    public StatusResponse buildOkWithWinnerCode(String winnerCode) {
        return StatusResponse.builder()
                .winnerCode(winnerCode)
                .status("OK")
                .build();
    }

    public StatusResponse buildWithMessage(String message) {
        return StatusResponse.builder()
                .status(message)
                .build();
    }
}
