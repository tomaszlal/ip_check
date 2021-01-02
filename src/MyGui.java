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

  

}
