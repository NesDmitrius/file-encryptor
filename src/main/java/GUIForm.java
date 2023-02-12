import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GUIForm {
    private JPanel rootPanel;
    private JTextField filePath;
    private JButton selectButton;
    private JButton actionButton;
    private File selectedFile;
    private boolean encryptedFileSelected = false;
    private final static String unencryptedAction = "Расшифровать";
    private final static String encryptAction = "Зашифровать";

    public GUIForm() {

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.showOpenDialog(rootPanel);
                selectedFile = chooser.getSelectedFile();
                onFileSelect();
            }
        });

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null) {
                    return;
                }
                String password = JOptionPane.showInputDialog("Введите пароль");
                if (password == null || password.length() == 0) {
                    showWrongMessage("Пароль не указан!");
                    return;
                }
                if (encryptedFileSelected) {
                    unencryptedFile(password);
                } else {
                    encryptFile(password);
                }
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setButtonEnable(boolean enable) {
        selectButton.setEnabled(enable);
        actionButton.setEnabled(enable);
    }

    private void encryptFile(String password) {
        EncryptorThread thread = new EncryptorThread(this);
        thread.setFile(selectedFile);
        thread.setPassword(password);
        thread.start();
    }

    private void unencryptedFile(String password) {
        UnencryptedThread thread = new UnencryptedThread(this);
        thread.setFile(selectedFile);
        thread.setPassword(password);
        thread.start();
    }

    private void onFileSelect() {
        if (selectedFile == null) {
            filePath.setText("");
            actionButton.setVisible(false);
            return;
        }
        filePath.setText(selectedFile.getAbsolutePath());
        try {
            ZipFile zipFile = new ZipFile(selectedFile);
            encryptedFileSelected = zipFile.isValidZipFile() && zipFile.isEncrypted();
            actionButton.setText(encryptedFileSelected ?
                    unencryptedAction : encryptAction);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        actionButton.setVisible(true);
    }

    public void showWrongMessage(String message) {
        JOptionPane.showMessageDialog(rootPanel, message, "Ошибка", JOptionPane.WARNING_MESSAGE);
    }

    public void showFinished() {
        JOptionPane.showMessageDialog(rootPanel,
                encryptedFileSelected ? "Расшифровка завершена!" : "Шифрование завершено!",
                "Завершено",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
