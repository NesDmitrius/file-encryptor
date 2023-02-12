import net.lingala.zip4j.ZipFile;

import java.io.File;

public class UnencryptedThread extends Thread {

    private GUIForm form;
    private File file;
    private char[] password;
    private String outPath;

    public UnencryptedThread(GUIForm form) {
        this.form = form;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setPassword(String password) {
        this.password = password.toCharArray();
    }

    @Override
    public void run() {
        onStart();
        boolean isErrors = false;
        try {
            outPath = getOutPath();
            ZipFile zipFile = new ZipFile(file);
            zipFile.setPassword(password);
            zipFile.extractAll(outPath);
        } catch (Exception ex) {
            if (ex.getMessage().contains("Wrong password!")) {
                form.showWrongMessage("Пароль указан неверно!");
                isErrors = true;
            } else {
                form.showWrongMessage(ex.getMessage());
            }
        }
        onFinish(isErrors);
    }

    private void onStart() {
        form.setButtonEnable(false);
    }

    private void onFinish(boolean isErrors) {
        form.setButtonEnable(true);
        if (!isErrors) {
            form.showFinished();
        } else {
            try {
                File removeFile = new File(outPath);
                removeFile.delete();
            } catch (Exception e) {
                form.showWrongMessage("Попробуйте снова.");
            }
        }
    }

    private String getOutPath() {
        String path = file.getAbsolutePath().replaceAll("\\.zip$", "");
        for (int i = 1; ; i++) {
            String number = i > 1 ? Integer.toString(i) : "";
            String outPath = path + number;
            if (!new File(outPath).exists()) {
                return outPath;
            }
        }
    }
}
