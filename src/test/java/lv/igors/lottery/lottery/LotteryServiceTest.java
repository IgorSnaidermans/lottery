package lv.igors.lottery.lottery;

import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.lottery.dto.CheckStatusDTO;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    final String REG_CODE = "0502981392837465";
    final String EMAIL = "some@mail.com";
    final Byte AGE = 21;
    final Long LOTTERY_ID = 0L;
    final String TITLE = "title";
    final int LIMIT = 1000;
    @Spy
    List<Code> spiedList = new ArrayList<>();
    @Mock
    private LotteryDAOImpl lotteryDAOImpl;
    @Mock
    private CodeService codeService;
    private StatusResponseManager statusResponseManager;
    private LotteryService lotteryService;
    private RegistrationDTO registrationDTO;
    private CheckStatusDTO checkStatusDTO;
    private Lottery validLottery;
    private NewLotteryDTO newLotteryDTO;
    private LotteryIdDTO lotteryIdDTO;
    private Code code;

    @BeforeEach
    void setUp() {
        statusResponseManager = new StatusResponseManager();
        String time = "1998-05-02T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(time), ZoneId.of("UTC"));
        lotteryService = new LotteryService(codeService, lotteryDAOImpl, clock, statusResponseManager);

        newLotteryDTO = NewLotteryDTO.builder()
                .limit(LIMIT)
                .title(TITLE)
                .build();

        registrationDTO = RegistrationDTO.builder()
                .age(AGE)
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .email(EMAIL)
                .build();

        validLottery = Lottery.builder()
                .active(true)
                .participantsLimit(LIMIT)
                .participants(LIMIT - 1)
                .build();

        code = Code.builder()
                .participatingCode(REG_CODE)
                .lottery(validLottery)
                .ownerEmail(EMAIL)
                .build();

        checkStatusDTO = CheckStatusDTO.builder()
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .email(EMAIL)
                .build();

        lotteryIdDTO = new LotteryIdDTO();
        lotteryIdDTO.setLotteryId(LOTTERY_ID);
    }

    @Test
    void newLottery_ShouldSuccessfullyCreate() throws LotteryException {
        final String TITLE = "Title";

        when(lotteryDAOImpl.findByTitle(any())).thenThrow(LotteryException.class);

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void ShouldThrowException_BecauseNoLotteryFoundInRepository() throws LotteryException {
        when(lotteryDAOImpl.getLotteryById(any()))
                .thenThrow(LotteryException.class);

        assertThrows(LotteryException.class, () -> lotteryService.getLotteryById(any()));
    }

    @Test
    void stopRegistration_ShouldSucceed() throws LotteryException {
        when(lotteryDAOImpl.getLotteryById(any()))
                .thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.endRegistration(lotteryIdDTO);
        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void stopRegistration_ShouldFailDueToAlreadyStopped() throws LotteryException {
        validLottery.setActive(false);
        when(lotteryDAOImpl.getLotteryById(any()))
                .thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.endRegistration(lotteryIdDTO);
        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is inactive", statusResponse.getReason());
    }

    @Test
    void newLotteryTitle_ShouldAlreadyExist() throws LotteryException {
        final String TITLE = "Title";
        Lottery lottery = Lottery.builder().title(TITLE).build();

        when(lotteryDAOImpl.findByTitle(any())).thenReturn(lottery);

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Lottery title already exists", statusResponse.getReason());
        assertNull(statusResponse.getId());
    }

    @Test
    void registerCode_ShouldPass() throws LotteryException {
        when(lotteryDAOImpl.getLotteryById(any()))
                .thenReturn(validLottery);

        when(codeService.addCode(any())).thenReturn(StatusResponse.builder()
                .status("OK")
                .build());

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("OK", statusResponse.getStatus());
    }

    @Test
    void registerCodeShouldNotPass_DueToTooManyParticipants() throws LotteryException {
        Lottery lottery = Lottery.builder()
                .active(true)
                .participantsLimit(1000)
                .participants(1000)
                .build();

        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(lottery);

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Too many participants", statusResponse.getReason());
    }

    @Test
    void registerCodeShouldNotPass_DueToLotteryInactive() throws LotteryException {
        Lottery lottery = Lottery.builder()
                .active(false)
                .participantsLimit(1000)
                .participants(999)
                .build();

        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(lottery);

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is inactive", statusResponse.getReason());
    }

    @Test
    void getWinnerStatus_ShouldReturnWin() throws LotteryException {

        validLottery.setWinnerCode(code);

        when(codeService.checkWinnerCode(any(), any())).thenReturn(StatusResponse.builder()
                .status("WIN")
                .build());
        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("WIN", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnLose() throws LotteryException {
        validLottery.setWinnerCode(code);

        when(lotteryDAOImpl.getLotteryById(any()))
                .thenReturn(validLottery);

        when(codeService.checkWinnerCode(any(), any()))
                .thenReturn(StatusResponse.builder()
                        .status("Lose")
                        .build());

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("Lose", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnPending() throws LotteryException {
        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("PENDING", statusResponse.getStatus());
    }

    @Test
    void chooseWinner_ShouldReturnWinnerCode() throws LotteryException {

        validLottery.setActive(false);
        spiedList.add(code);
        validLottery.setRegisteredCodes(spiedList);
        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(validLottery);


        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
        assertNotNull(statusResponse.getWinnerCode());
    }

    @Test
    void chooseWinner_ShouldFailDueToActiveLottery() throws LotteryException {
        validLottery.setActive(true);
        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is active", statusResponse.getReason());
    }

    @Test
    void chooseWinner_ShouldFailDueToFinishedLottery() throws LotteryException {
        validLottery.setActive(false);
        validLottery.setWinnerCode(code);
        when(lotteryDAOImpl.getLotteryById(any())).thenReturn(validLottery);

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Lottery is finished", statusResponse.getReason());
    }
}

