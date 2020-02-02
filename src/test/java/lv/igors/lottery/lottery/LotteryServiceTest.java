package lv.igors.lottery.lottery;

import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeException;
import lv.igors.lottery.code.CodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    @Mock
    private LotteryDAO lotteryDAO;
    @Mock
    private CodeService codeService;
    private LotteryService lotteryService;
    private RegistrationDTO registrationDTO;
    private Lottery validLottery;


    @BeforeEach
    void setUp() {
        lotteryService = new LotteryService(lotteryDAO, codeService);

        final String REG_CODE = "0502981392837465";
        final String EMAIL = "some@mail.com";
        final Byte AGE = 21;
        final Long LOTTERY_ID = 0L;

        registrationDTO = RegistrationDTO.builder()
                .age(AGE)
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .email(EMAIL)
                .build();

        validLottery = Lottery.builder()
                .active(true)
                .limit(1000)
                .participants(999)
                .build();
    }

    @Test
    void newLottery_ShouldSuccessfullyCreate() {
        final String TITLE = "Title";

        when(lotteryDAO.findByTitle(TITLE)).thenReturn(Optional.empty());

        StatusResponse statusResponse = lotteryService.newLottery(TITLE, 1000);

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void newLotteryTitle_ShouldAlreadyExist(){
        final String TITLE = "Title";
        Lottery lottery = Lottery.builder().title(TITLE).build();

        when(lotteryDAO.findByTitle(TITLE)).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.newLottery(TITLE, 1000);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("This lottery title already exist", statusResponse.getReason());
        assertNull(statusResponse.getId());
    }

    @Test
    void registerCode_ShouldPass() throws LotteryException {

        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("OK", statusResponse.getStatus());
    }

    @Test
    void registerCodeShouldNotPass_DueToTooManyParticipants() throws LotteryException {
        Lottery lottery = Lottery.builder()
                .active(true)
                .limit(1000)
                .participants(1000)
                .build();

        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Too many participants", statusResponse.getReason());
    }

    @Test
    void registerCodeShouldNotPass_DueToLotteryInactive() throws LotteryException {
        Lottery lottery = Lottery.builder()
                .active(false)
                .limit(1000)
                .participants(999)
                .build();

        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(lottery));

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("Registration is not started", statusResponse.getReason());
    }


    @Test
    void getWinnerStatus_ShouldReturnWin() throws CodeException {
        Code code = Code.builder()
                .participatingCode(registrationDTO.getCode())
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .build();
        validLottery.setWinnerCode(registrationDTO.getCode());

        when(codeService.checkWinnerCode(code, registrationDTO.getCode())).thenReturn(true);
        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.getWinnerStatus(code);

        assertEquals("WIN", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnLose() throws CodeException {
        Code code = Code.builder()
                .participatingCode("123")
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .build();
        validLottery.setWinnerCode(registrationDTO.getCode());

        when(codeService.checkWinnerCode(code, registrationDTO.getCode())).thenReturn(false);
        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.getWinnerStatus(code);

        assertEquals("LOSE", statusResponse.getStatus());
    }

    @Test
    void getWinnerStatus_ShouldReturnPending(){
        Code code = Code.builder()
                .participatingCode(registrationDTO.getCode())
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .build();

        when(lotteryDAO.findById(registrationDTO.getLotteryId())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.getWinnerStatus(code);

        assertEquals("PENDING", statusResponse.getStatus());
    }


    @Spy
    List<Code> spiedList = new ArrayList<>();

    @Test
    void chooseWinner_ShouldReturnWinnerCode() throws LotteryException {
        validLottery.setActive(false);
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));
        Code code = Code.builder()
                .participatingCode(registrationDTO.getCode())
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .build();

        for(int i=0;i<validLottery.getParticipants();i++){
            spiedList.add(code);
        }

        when(codeService.getAllCodesByLotteryId(validLottery.getId())).thenReturn(spiedList);

        StatusResponse statusResponse = lotteryService.chooseWinner(validLottery.getId());

        assertEquals("OK", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
        assertNotNull(statusResponse.getWinnerCode());
    }

    @Test
    void chooseWinner_ShouldFailDueToActiveLottery() throws LotteryException {
        validLottery.setActive(true);
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.chooseWinner(validLottery.getId());

        assertEquals("FAIL", statusResponse.getStatus());
        assertEquals("Lottery is active", statusResponse.getReason());
    }

    @Test
    void chooseWinner_ShouldFailDueToFinishedLottery() throws LotteryException {
        validLottery.setActive(false);
        validLottery.setWinnerCode("123456789");
        when(lotteryDAO.findById(any())).thenReturn(Optional.ofNullable(validLottery));

        StatusResponse statusResponse = lotteryService.chooseWinner(validLottery.getId());

        assertEquals("FAIL", statusResponse.getStatus());
        assertEquals("Lottery is finished", statusResponse.getReason());
    }
}

