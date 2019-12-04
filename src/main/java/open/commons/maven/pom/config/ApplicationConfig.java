/*
 *
 * This file is generated under this project, "maven-pom".
 *
 * Date  : 2019. 12. 4. 오후 2:17:08
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.maven.pom.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import open.commons.maven.pom.LibraryTarget;

/**
 * 
 * @since 2019. 12. 4.
 * @version
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
@Configuration
public class ApplicationConfig {

    public static final String BEAN_QUALIFIER_LIBRARIES = "open.commons.maven.pom.config.ApplicationConfig.LibraryTarget";

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 12. 4.     박준홍         최초 작성
     * </pre>
     *
     * @since 2019. 12. 4.
     * @version
     */
    public ApplicationConfig() {
    }

    @Bean(value = BEAN_QUALIFIER_LIBRARIES)
    @Primary
    @ConfigurationProperties("maven.targets")
    public List<LibraryTarget> getLibraries() {
        return new ArrayList<LibraryTarget>();
    }
}
