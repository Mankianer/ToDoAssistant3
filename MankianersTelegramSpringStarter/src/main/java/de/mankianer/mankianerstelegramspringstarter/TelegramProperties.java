package de.mankianer.mankianerstelegramspringstarter;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("telegram.bot")
public class TelegramProperties {

    /**
     * A message for the service.
     */
    private String token;

    private String username;

    private List<String> allowedUsernames;

}
