package org.jom.Database;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Backup implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    private ServletContext context;
    private boolean backupStatus = true;
    final private LocalTime fixedAtMidnight = LocalTime.of(0, 0, 0);

    final private String ip = "localhost";
    final private String port = "3306";
    final private String database = "jom_db";
    final private String user = "root";
    final private String password = "zaq123";

    public void contextInitialized(ServletContextEvent contextEvent) {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::performBackup, calculateInitialDelay(), 24, TimeUnit.HOURS);
        context = contextEvent.getServletContext();
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().atTime(fixedAtMidnight);
        if (now.isAfter(nextMidnight)) {
            nextMidnight = nextMidnight.plusDays(1);
        }
        return now.until(nextMidnight, ChronoUnit.SECONDS);
    }

    private void performBackup() {
        try {
            if (backupStatus) {
                backupStatus = false;
                String projectFolderPath = "C:\\Users\\CHAMA COMPUTERS\\Desktop\\Campus\\Backup\\";
                LocalDateTime now = LocalDateTime.now();
                String path = projectFolderPath + "eduClickDbBackup" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + ".sql";
                String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + password;
                Runtime runtime = Runtime.getRuntime();
                File databaseFile = new File(path);
                try {
                    Process process = runtime.exec(dumpCommand);
                    try (PrintStream printStream = new PrintStream(databaseFile);
                         InputStream inputStream = process.getInputStream()) {
                        int nextByteOfData;
                        while ((nextByteOfData = inputStream.read()) != -1) {
                            printStream.write(nextByteOfData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            backupStatus = true;
        }
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
        scheduler.shutdownNow();
        contextEvent.getServletContext().log("ScheduledExecutorService has been shutdown.");
    }
}
