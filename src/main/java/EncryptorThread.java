import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;

public class EncryptorThread extends Thread {

    private final GUIForm form;
    private File file;
    private char[] password;
    private final ZipParameters parameters = ParametersContainer.getParameters();

    public EncryptorThread(GUIForm form) {
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
        try {
            String archiveName = getArchiveName();
            ZipFile zipFile = new ZipFile(archiveName, password);
            if (file.isDirectory()) {
                zipFile.addFolder(file, parameters);
            } else {
                zipFile.addFile(file, parameters);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        onFinish();
    }

    private void onStart() {
        form.setButtonEnable(false);
    }

    private void onFinish() {
        form.setButtonEnable(true);
        form.showFinished();
    }

    private String getArchiveName() {
        for (int i = 1; ; i++) {
            String number = i > 1 ? Integer.toString(i) : "";
            String archiveName = file.getAbsolutePath() + number + ".zip";
            if (!new File(archiveName).exists()) {
                return archiveName;
            }
        }
    }
}
