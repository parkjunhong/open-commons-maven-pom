/*
 *
 * This file is generated under this project, "maven-pom".
 *
 * Date  : 2019. 12. 4. 오후 2:19:15
 *
 * Author: Park_Jun_Hong_(fafanmama_at_naver_com)
 * 
 */

package open.commons.maven.pom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import open.commons.maven.pom.config.ApplicationConfig;
import open.commons.text.NamedTemplate;
import open.commons.utils.FileUtils;
import open.commons.utils.IOUtils;

/**
 * 
 * @since 2019. 9. 9.
 * @version
 * @author Park_Jun_Hong_(fafanmama_at_naver_com)
 */
@Component
public class POMGenerator implements CommandLineRunner, InitializingBean {

    static final FilenameFilter JARFILE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };

    private final String ARTIFACT_ID_REF = "$file_name$";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(ApplicationConfig.BEAN_QUALIFIER_LIBRARIES)
    private List<LibraryTarget> libraries;

    @Value("${maven.pom.template}")
    private String pomTemplateFilepath;
    private String pomTemplateStr;

    /**
     * <br>
     * 
     * <pre>
     * [개정이력]
     *      날짜      | 작성자   |   내용
     * ------------------------------------------
     * 2019. 9. 9.      박준홍         최초 작성
     * </pre>
     *
     * @since 2019. 9. 9.
     * @version
     */
    public POMGenerator() {
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.pomTemplateStr = new String(IOUtils.readFully(new FileInputStream(this.pomTemplateFilepath)));
    }

    private void createPOM(LibraryTarget target, File file) {
        String modelVersion = target.getModelVersion();
        String groupId = target.getGroupId();
        String artifactId = target.getArtifactId();
        String version = target.getVersion();
        String desc = target.getDescription();

        // #0. description 정보 적용
        NamedTemplate tpl = new NamedTemplate(pomTemplateStr);
        tpl.addValue("description", desc);
        // #1. 나머지 속성 적용
        tpl = new NamedTemplate(tpl.format());

        // #2. artifactId 확인
        if (ARTIFACT_ID_REF.equals(artifactId)) {
            artifactId = FileUtils.getFileNameNoExtension(file);
        }

        String pom = tpl //
                .addValue("modelVersion", modelVersion) //
                .addValue("groupId", groupId) //
                .addValue("artifactId", artifactId) //
                .addValue("version", version) //
                .format();

        File outfile = new File(file.getParentFile(), String.format("%s-%s.pom", artifactId, version));
        if (!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            IOUtils.transfer(new ByteArrayInputStream(pom.getBytes()), new FileOutputStream(outfile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void handleDirectory(LibraryTarget target) {
        File dir = new File(target.getFilepath());
        File[] jarfiles = dir.listFiles(JARFILE_FILTER);

        Arrays.asList(jarfiles).forEach(file -> {
            createPOM(target, file);
        });
    }

    /**
     * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
     */
    @Override
    public void run(String... args) throws Exception {

        this.libraries.forEach(target -> {
            File targetfile = new File(target.getFilepath());

            if (!targetfile.exists()) {
                logger.warn("target does NOT exists. Pathanem={}", targetfile.getAbsolutePath());
                return;
            }

            logger.info("target exists. Pathanem={}", targetfile.getAbsolutePath());

            if (targetfile.isFile()) {
                createPOM(target, targetfile);
            } else if (targetfile.isDirectory()) {
                handleDirectory(target);
            }
        });
    }
}
