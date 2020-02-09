package lv.igors.lottery;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
public class DataSourceConfiguration {
    private static final String DB_URL = "jdbc:postgresql://ec2-54-246-90-10.eu-west-1.compute.amazonaws.com:5432/d92gsv76e7aln9";
    private static final String DB_USER = "htohdzivmrxshe";
    private static final String DB_PASSWORD = "042a0348ad89d3eba5745f5afe025b84841fe9a0ec8166af2ad244a92d3800ec";
    private static final String DB_DRIVER = "org.postgresql.Driver";

    @Bean
    DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        ds.setDriverClassName(DB_DRIVER);
        ds.setMaxTotal(3);
        return ds;
    }
}
