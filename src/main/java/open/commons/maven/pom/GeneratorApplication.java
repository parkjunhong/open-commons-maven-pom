/*
 *
 * This file is generated under this project, "maven-pom".
 *
 * Date  : 2019. 12. 4. 오후 2:18:08
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.maven.pom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 
 * @since 2019. 12. 4.
 * @version
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
@ServletComponentScan
@SpringBootApplication
public class GeneratorApplication {

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
    public GeneratorApplication() {
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GeneratorApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}