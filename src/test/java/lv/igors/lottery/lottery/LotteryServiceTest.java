package lv.igors.lottery.lottery;

import lv.igors.lottery.lottery.dto.CheckStatusDTO;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    @Mock
    private LotteryDAO lotteryDAO;
    @Mock
    private CodeService codeService;

    private LotteryService lotteryService;
    private RegistrationDTO registrationDTO;
    private Lottery validLottery;
    private NewLotteryDTO newLotteryDTO;
    private CheckStatusDTO checkStatusDTO;
    private LotteryIdDTO lotteryIdDTO;
    final String REG_CODE = "0502981392837465";
    final String EMAIL = "some@mail.com";
    final Byte AGE = 21;
    final Long LOTTERY_ID = 0L;
    final String TITLE = "title";
    final int LIMIT = 1000;


    @BeforeEach
    void setUp() {
        String time = "1998-05-02T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(time), ZoneId.of("UTC"));
        lotteryService = new LotteryService(lotteryDAO, codeService, clock);

        newLotteryDTO = NewLotteryDTO.builder()
                .limit(LIMIT)
                .title(TITLE)
                .build();

        checkStatusDTO = CheckStatusDTO.builder()
                .code(REG_CODE)
                .email(EMAIL)
                .lotteryId(LOTTERY_ID)
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
                .participants(LIMIT-1)
                .build();

        lotteryIdDTO = new LotteryIdDTO();
        lotteryIdDTO.setLotteryId(LOTTERY_ID);
    }

    @Test
    void newLottery_ShouldSuccessfullyCreate(){
        final String TITLE = "Title";

        when(lotteryDAO.findByTitle(any())).thenReturn(Optional.empty());

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void ShouldThrowException_BecauseNoLotteryFoundInRepository(){
        when(lotteryDAO.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(LotteryException.class, () -> lotteryService.getLotteryById(any()));
    }

    @Test
    void stopRegistration_ShouldSucceed(){
        when(lotteryDAO.findById(any()))
                .thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryIdDTO);
        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void stopRegistration_ShouldFailDueToAlreadyStopped(){
        validLottery.setActive(false);
        when(lotteryDAO.findById(any()))
                .thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryIdDTO);
        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is inactive", statusResponse.getReason());
    }

    @Test
    void newLotteryTitle_ShouldAlreadyExist(){
        final String TITLE = "Title";
        Lottery lottery = Lottery.builder().title(TITLE).build();

        when(lotteryDAO.findByTitle(any())).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("This lottery title already exist", statusResponse.getReason());
        assertNull(statusResponse.getId());
    }

    @Test
    void registerCode_ShouldPass(){
        when(lotteryDAO.findById(any()))
                .thenReturn(Optional.ofNullable(validLottery));

        when(codeService.addCode(any())).thenReturn(StatusResponse.builder()
                .status("OK")
                .build());

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("OK", statusResponse.getStatus());
    }

    @Test
    void registerCodeShouldNotPass_DueToTooManyParticipants(){
        Lottery lottery = Lottery.builder()
                .active(true)
                .participantsLimit(1000)
                .participants(1000)
                .build();

        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Too many participants", statusResponse.getReason());
    }

    @Test
    void registerCodeShouldNotPass_DueToLotteryInactive(){
        Lottery lottery = Lottery.builder()
                .active(false)
                .participantsLimit(1000)
                .participants(999)
                .build();

        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is inactive", statusResponse.getReason());
    }


    @Test
    void getWinnerStatus_ShouldReturnWin() {

        validLottery.setWinnerCode(registrationDTO.getCode());

        when(codeService.checkWinnerCode(any(), any())).thenReturn(StatusResponse.builder()
                .status("WIN")
                .build());
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));
        when(codeService.isCodeValid(any())).thenReturn(true);

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("WIN", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnLose() {
        validLottery.setWinnerCode(registrationDTO.getCode());

        when(lotteryDAO.findById(any()))
                .thenReturn(Optional.ofNullable(validLottery));

        when(codeService.checkWinnerCode(any(), any()))
                .thenReturn(StatusResponse.builder()
                        .status("Lose")
                        .build());

        when(codeService.isCodeValid(any())).thenReturn(true);

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("Lose", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnPending() {
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));
        when(codeService.isCodeValid(any())).thenReturn(true);

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);

        assertEquals("PENDING", statusResponse.getStatus());
    }


    @Spy
    List<Code> spiedList = new ArrayList<>();

    @Test
    void chooseWinner_ShouldReturnWinnerCode(){
        validLottery.setActive(false);
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));
        Code code = Code.builder()
                .participatingCode(registrationDTO.getCode())
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .build();

        for (int i = 0; i < validLottery.getParticipants(); i++) {
            spiedList.add(code);
        }

        when(codeService.getAllCodesByLotteryId(lotteryIdDTO.getLotteryId())).thenReturn(spiedList);

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
        assertNotNull(statusResponse.getWinnerCode());
    }

    @Test
    void chooseWinner_ShouldFailDueToActiveLottery(){
        validLottery.setActive(true);
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is active", statusResponse.getReason());
    }

    @Test
    void chooseWinner_ShouldFailDueToFinishedLottery(){
        validLottery.setActive(false);
        validLottery.setWinnerCode("123456789");
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryIdDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Lottery is finished", statusResponse.getReason());
    }
}

