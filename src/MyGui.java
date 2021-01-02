import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.Keymap;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;


public class MyGui {
    private JMenuItem menuItemCopy;
    private JPopupMenu popupMenu;
    private JPanel panelMain;
    private JButton buttonToCalculate;
    private JTextField insertedIP;
    private JComboBox selectedMask;
    private JPanel panelDane;
    private JPanel panelButton;
    private JPanel panelBottom;
    private JPanel PanelUp;
    private JTextField fieldIP;
    private JTextField fieldMask;
    private JTextField fieldNetAdres;
    private JTextField fieldBroadcast;
    private JTextField fieldHosts;
    private JTextField fieldHostMin;
    private JTextField fieldHostMax;
    private JPanel panelHostminmax;
    private JLabel labelError;
    String[] masksNet = {"/0 - 0.0.0.0", "/1 - 128.0.0.0", "/2 - 192.0.0.0", "/3 - 224.0.0.0", "/4 - 240.0.0.0"
            , "/5 - 248.0.0.0", "/6 - 252.0.0.0", "/7 - 254.0.0.0", "/8 - 255.0.0.0", "/9 - 255.128.0.0"
            , "/10 - 255.192.0.0", "/11 - 255.224.0.0", "/12 - 255.240.0.0", "/13 - 255.248.0.0"
            , "/14 - 255.252.0.0.", "/15 - 255.254.0.0", "/16 - 255.255.0.0", "/17 - 255.255.128.0"
            , "/18 - 255.255.194.0", "/19 - 255.255.224.0", "/20 - 255.255.240.0", "/21 - 255.255.248.0"
            , "/22 - 255.255.252.0", "/23 - 255.255.254.0", "/24 - 255.255.255.0", "/25 - 255.255.255.128"
            , "/26 - 255.255.255.192", "/27 - 255.255.255.224", "/28 - 255.255.255.240", "/29 - 255.255.255.248"
            , "/30 - 255.255.255.252"};


    public MyGui() {
        popupMenu = new JPopupMenu();  // menu podreczne z poleceniem kopiuj
        menuItemCopy = new JMenuItem();


        menuItemCopy.setText("Kopiuj");
        menuItemCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionCopyMenu1(evt);

            }
        }); // dodanie listenera do menu kopiuj w menu podręcznym


        popupMenu.add(menuItemCopy);
        fieldNetAdres.setComponentPopupMenu(popupMenu); //ustawienie menu podrecznego do pol tekstowych
        fieldBroadcast.setComponentPopupMenu(popupMenu);
        fieldHostMax.setComponentPopupMenu(popupMenu);
        fieldHostMin.setComponentPopupMenu(popupMenu);
        fieldIP.setComponentPopupMenu(popupMenu);
        fieldHosts.setComponentPopupMenu(popupMenu);
        fieldMask.setComponentPopupMenu(popupMenu);


        for (String maskaNr : masksNet) {
            selectedMask.addItem(maskaNr);
        }
        selectedMask.setSelectedIndex(24);


        buttonToCalculate.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obliczIP();
                //System.out.println("enter");
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //ustawiienie i przypisanie akcji na przycisku Oblicz  naciśnięciem "enter"

        buttonToCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obliczIP();
            }
        });
    }

    public void obliczIP() {
        int ip, maska, adresSieci, adresRozgloszeniowy, hostFirst, hostLast, ilosc;

        char[] charTabIP = insertedIP.getText().trim().toCharArray();
        for (int i = 0; i < charTabIP.length; i++) {
            if (charTabIP[i] == ',') charTabIP[i] = '.';
        }
        String tempIpField = new String(charTabIP);

        insertedIP.setText(tempIpField);
        insertedIP.selectAll();

        if (validateIp(insertedIP.getText())) {
            ip = ipToBits(insertedIP.getText());
            maska = maskaSieciowaBin(selectedMask.getSelectedIndex());
            adresSieci = addressNet(ip, maska);
            adresRozgloszeniowy = addressBroadcast(adresSieci, maska);
            hostFirst = firstAdr(adresSieci);
            hostLast = lastAdr(adresRozgloszeniowy);
            ilosc = adresRozgloszeniowy - hostFirst;


            labelError.setText("");
            fieldIP.setText(ipToString(ip));
            fieldMask.setText(ipToString(maska));
            fieldNetAdres.setText(ipToString(adresSieci) + "/" + selectedMask.getSelectedIndex());
            fieldBroadcast.setText(ipToString(adresRozgloszeniowy));
            fieldHostMin.setText(ipToString(hostFirst));
            fieldHostMax.setText(ipToString(hostLast));
            if (ilosc < 0) fieldHosts.setText("Zbyt wiele hostów");
            else fieldHosts.setText(Integer.toString(ilosc));


        } else {
            labelError.setText("Błędny adres IP");
            fieldIP.setText("");
            fieldMask.setText("");
            fieldNetAdres.setText("");
            fieldBroadcast.setText("");
            fieldHostMin.setText("");
            fieldHostMax.setText("");
            fieldHosts.setText("");
        }
    }

    private void actionCopyMenu1(ActionEvent evt) {
        //System.out.println("popmenu");
        //System.out.println(popupMenu.getInvoker().toString());
        copy((JTextField) popupMenu.getInvoker()); //Rzutowanie do jtextfield z jcomponent.  metoda getInvoker Zwraca komponent, który jest „wywołującym” tego popupmenu
    }

    private void copy(JTextField poleText) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//pobranie schowka systemowego
        String text = poleText.getSelectedText();
        if (text == null) text = poleText.getText();
        StringSelection selection = new StringSelection(text);
        clip.setContents(selection, null);
    }

    private int lastAdr(int addressBr) {
        return addressBr - 0b1;
    }

    private int firstAdr(int addressNet) {
        return addressNet + 0b1;
    }

    private int addressBroadcast(int adressNet, int maskaBin) {
        return adressNet + (~maskaBin);
    }

    private int addressNet(int ipBin, int maskaBin) {
        return ipBin & maskaBin;
    }

    private int maskaSieciowaBin(int iloscBitow) {
        if (iloscBitow == 0) return 0;
        else return (1 << 31) >> iloscBitow - 1;
    }


    private boolean validateIp(String ipAdress) {
        try {
            String[] groups = ipAdress.split("\\.");
            if (groups.length == 4) {
                for (String x : groups) {
                    int liczba = Integer.parseInt(x);
                    //System.out.println(liczba);
                    if (liczba < 0 || liczba > 255) return false;
                }
                return true;
            } else {
                //System.out.println("Nie ip");
                return false;
            }
        } catch (Exception e) {
            return false;
        }


    }

    public static String ipToString(int ipBinTemp) // zamienia adres ip zapisany binarnie w int do postaci tekstu
    {
        int tempOctet = 0;
        String ipString = "";
        for (int i = 0; i < 25; i = i + 8) {
            tempOctet = ipBinTemp << i;
            tempOctet = tempOctet >>> 24;
            ipString = ipString + Integer.toString(tempOctet);
            if (i < 24) ipString = ipString + ".";
        }
        return ipString;
    }

    public static int ipToBits(String ipAdress)  //zamienia string zapisany w formacie xxx.xxx.xxx.xxx na binarnie w liczbie int
    {
        int IpBinTemp = 0;
        String[] groups = ipAdress.trim().split("\\.");
        if (groups.length == 4) {
            for (String x : groups) {
                IpBinTemp = IpBinTemp << 8;
                IpBinTemp = IpBinTemp | Integer.parseInt(x);
            }
        }
        return IpBinTemp;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MyGui");
        frame.setContentPane(new MyGui().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(640, 330);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(5, 5, 5, 5), -1, -1));
        panelDane = new JPanel();
        panelDane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelDane, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Courier New", Font.BOLD, 16, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Wybierz maskę sieci");
        panelDane.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Courier New", Font.BOLD, 16, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setHorizontalAlignment(10);
        label2.setHorizontalTextPosition(11);
        label2.setText("Podaj IP");
        panelDane.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        insertedIP = new JTextField();
        Font insertedIPFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, insertedIP.getFont());
        if (insertedIPFont != null) insertedIP.setFont(insertedIPFont);
        panelDane.add(insertedIP, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        selectedMask = new JComboBox();
        Font selectedMaskFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, selectedMask.getFont());
        if (selectedMaskFont != null) selectedMask.setFont(selectedMaskFont);
        panelDane.add(selectedMask, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelError = new JLabel();
        labelError.setFocusTraversalPolicyProvider(false);
        Font labelErrorFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, labelError.getFont());
        if (labelErrorFont != null) labelError.setFont(labelErrorFont);
        labelError.setForeground(new Color(-65536));
        labelError.setText("");
        panelDane.add(labelError, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panelDane.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(150, -1), new Dimension(150, -1), null, 0, false));
        panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelMain.add(panelButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonToCalculate = new JButton();
        buttonToCalculate.setText("Oblicz");
        panelButton.add(buttonToCalculate);
        panelBottom = new JPanel();
        panelBottom.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelBottom, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Adres IP");
        panelBottom.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldIP = new JTextField();
        fieldIP.setEditable(false);
        Font fieldIPFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, fieldIP.getFont());
        if (fieldIPFont != null) fieldIP.setFont(fieldIPFont);
        panelBottom.add(fieldIP, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Maska");
        panelBottom.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Adres sieci");
        panelBottom.add(label5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setText("Adres rozgłoszeniowy");
        panelBottom.add(label6, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panelBottom.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        fieldMask = new JTextField();
        fieldMask.setEditable(false);
        Font fieldMaskFont = this.$$$getFont$$$("Courier New", -1, 16, fieldMask.getFont());
        if (fieldMaskFont != null) fieldMask.setFont(fieldMaskFont);
        panelBottom.add(fieldMask, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fieldNetAdres = new JTextField();
        fieldNetAdres.setEditable(false);
        Font fieldNetAdresFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, fieldNetAdres.getFont());
        if (fieldNetAdresFont != null) fieldNetAdres.setFont(fieldNetAdresFont);
        panelBottom.add(fieldNetAdres, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fieldBroadcast = new JTextField();
        fieldBroadcast.setEditable(false);
        Font fieldBroadcastFont = this.$$$getFont$$$("Courier New", Font.BOLD, 16, fieldBroadcast.getFont());
        if (fieldBroadcastFont != null) fieldBroadcast.setFont(fieldBroadcastFont);
        panelBottom.add(fieldBroadcast, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fieldHosts = new JTextField();
        fieldHosts.setEditable(false);
        Font fieldHostsFont = this.$$$getFont$$$("Courier New", -1, 16, fieldHosts.getFont());
        if (fieldHostsFont != null) fieldHosts.setFont(fieldHostsFont);
        panelBottom.add(fieldHosts, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Hostów w sieci");
        panelBottom.add(label7, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$("Courier New", Font.BOLD | Font.ITALIC, 16, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Host min. - max.");
        panelBottom.add(label8, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelHostminmax = new JPanel();
        panelHostminmax.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelBottom.add(panelHostminmax, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fieldHostMin = new JTextField();
        fieldHostMin.setEditable(false);
        Font fieldHostMinFont = this.$$$getFont$$$("Courier New", -1, 16, fieldHostMin.getFont());
        if (fieldHostMinFont != null) fieldHostMin.setFont(fieldHostMinFont);
        panelHostminmax.add(fieldHostMin, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText(" - ");
        panelHostminmax.add(label9, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldHostMax = new JTextField();
        fieldHostMax.setEditable(false);
        Font fieldHostMaxFont = this.$$$getFont$$$("Courier New", -1, 16, fieldHostMax.getFont());
        if (fieldHostMaxFont != null) fieldHostMax.setFont(fieldHostMaxFont);
        panelHostminmax.add(fieldHostMax, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        PanelUp = new JPanel();
        PanelUp.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(PanelUp, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$("Courier New", Font.BOLD, 20, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Kalkulator adresów IP");
        PanelUp.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panelMain.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
