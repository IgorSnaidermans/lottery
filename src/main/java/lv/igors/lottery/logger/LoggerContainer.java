package lv.igors.lottery.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LoggerContainer {
    List<Logger> loggerList = new ArrayList<>();

    public Logger findLogger(Class clazz) {
        Optional<Logger> optionalLogger = scanLoggerList(clazz);
        return optionalLogger.orElseGet(() -> createNecessaryLogger(clazz));
    }

    private Optional<Logger> scanLoggerList(Class clazz) {
        for (Logger logger : loggerList) {
            if (logger.getClass().equals(clazz)) {
                return Optional.of(logger);
            }
        }
        return Optional.empty();
    }

    private Logger createNecessaryLogger(Class clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        loggerList.add(logger);
        return logger;
    }
}
