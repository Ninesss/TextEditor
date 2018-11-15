//Author:Chenxiao ZHi
//Cite:Siyu Chen(Max)
//I don't know how to call terminal on mac. so...

package texteditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame{
    private JTextField textf; //field
    private JTextArea texta; //area
    private JTextArea textres;
    private JMenuBar menubar;
    private JMenu mFile;
    private JMenu mBuild;
    private JMenuItem New;
    private JMenuItem Open;
    private JMenuItem Save;
    private JMenuItem Quit;
    private JMenuItem Compile;
    private JMenuItem Run;
    private JFileChooser chooser;
    private String str_pkg;
    private String str_bef;
    private String str_path;
    private String str_filename1;
    private String str_filename2;
    private StringBuilder strb_content;
    private DialogFrame dialog_frame;
    private String strPath;
    private List<String> regexSplitLine;  // All the matched part of error message using regex
    private List<Integer> regexSplitLineInt;   // All the num of matched error message rows
    private int cursorLineNum = 0;
    
    public TextEditor(){
    super("TextEditor");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container c = getContentPane();
    c.setBackground(Color.WHITE);
    setSize(800,600);
    Font f = new Font("Times", Font.PLAIN, 24);
    Font g = new Font("Times", Font.PLAIN, 18);
    
    textf = new JTextField();
    textf.setFont(f);
    c.add(textf,BorderLayout.NORTH);
    
    texta = new JTextArea();
    texta.setFont(f);
    c.add(texta,BorderLayout.CENTER);
    JScrollPane scrollPane = new JScrollPane(texta);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    c.add(scrollPane);
    
    textres = new JTextArea();
    textres.setFont(f);
    c.add(textres,BorderLayout.SOUTH);
    
    menubar = new JMenuBar();
    setJMenuBar(menubar);
    
    
    mFile = new JMenu("File");
    mFile.setFont(f);
    menubar.add(mFile);
    
    mBuild = new JMenu("Build");
    mBuild.setFont(f);
    menubar.add(mBuild);
    
    New = new JMenuItem("New");
    New.setFont(g);
    New.addActionListener(new ActionNew());
    mFile.add(New);
    
    Open = new JMenuItem("Open");
    Open.setFont(g);
    Open.addActionListener(new ActionOpen());
    mFile.add(Open);
    
    Save = new JMenuItem("Save");
    Save.setFont(g);
    Save.addActionListener(new ActionSave());
    mFile.add(Save);
    
    Quit = new JMenuItem("Quit");
    Quit.setFont(g);
    Quit.addActionListener(new ActionQuit());
    mFile.add(Quit);
    
    Compile = new JMenuItem("Compile");
    Compile.setFont(g);
    Compile.addActionListener(new ActionCompile());
    mBuild.add(Compile);
    
    Run = new JMenuItem("Run");
    Run.setFont(g);
    mBuild.add(Run);
    
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);

    regexSplitLine = new ArrayList<>();
    regexSplitLineInt = new ArrayList<>();

    texta.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F4:{
                if(!regexSplitLineInt.isEmpty()){
                    showNextErrorLine();
                }
                //System.out.println("xia");
                break;
            }
        }
    }
});

    this.setVisible(true);
    }
    
    public void textReader()throws FileNotFoundException,IOException{
        BufferedReader br = new BufferedReader(new FileReader(str_pkg));
        String line;
        strb_content = new StringBuilder();
        while ((line = br.readLine())!= null){
            strb_content.append(line+"\n");
        }
        textf.setText(str_pkg);
        texta.setText(strb_content.toString());
        str_bef = strb_content.toString();
    }
    
    public void textWriter()throws IOException{
        PrintWriter p = new PrintWriter(new FileWriter(str_pkg));
        String s = texta.getText();
        p.print(s);
        str_bef = s;
        p.close();
    }
    
    public void callQuitDialog(){
        int result = JOptionPane.showConfirmDialog(
                this, "Do you want to SAVE before quit？",
                "Dialog", JOptionPane.YES_NO_CANCEL_OPTION
        );
        // result = 0 -> SAVE and QUIT    result = 1 -> QUIT WITHOUT SAVE
        // result = -1 , 2 -> CANCEL
        if(result == 0){
            try {
                textWriter();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }
        if(result == 1)
            System.exit(0);
    }
    
    class DialogFrame extends JFrame{
        JPanel jp;
        JTextField jtf1;
        JTextField jtf2;
        JButton jb1;
        JButton jb2;
        String text_directories;
        String text_filename;
        public DialogFrame() {
            jp = new JPanel();
            JLabel jl1 = new JLabel("Please enter a file name");
            JLabel jl2 = new JLabel("Directories");
            jtf1 = new JTextField("MyClass.java",40);
            jtf2 = new JTextField(30);
            jb1 = new JButton("Choose Directories");
            jb2 = new JButton("Confirm");
            jp.add(jl1);
            jp.add(jtf1);
            jp.add(jl2);
            jp.add(jtf2);
            jp.add(jb1);
            jp.add(jb2);
            add(jp);
            jb1.addActionListener(new ActionListener_Choose_Directories());
            jb2.addActionListener(new ActionListener_ConfirmDialog());
            //
            setTitle("Create a new .java file");//
            setSize(380, 180);//
            setLocationRelativeTo(null);//
            setVisible(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);//
        }

        class ActionListener_Choose_Directories implements ActionListener {
            public void actionPerformed(ActionEvent e){
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.showDialog(new JLabel(), "Choose Directories");
                File file = chooser.getSelectedFile();
                text_directories =  file.getAbsolutePath();
                jtf2.setText(text_directories);
                //text_field.setText(text_directories);
            }
        }
        
        class ActionListener_ConfirmDialog implements ActionListener{
            public void actionPerformed(ActionEvent e){
                text_directories = jtf2.getText();
                text_filename = jtf1.getText();
                String home = System.getProperty("user.home");
                File file = new File(text_directories , text_filename);
                System.out.println(text_directories + " " + text_filename);
                try {
                    if(!file.exists()) {
                        boolean b = file.createNewFile();
                        System.out.println(b);
                        str_pkg = file.getAbsolutePath();
                        str_filename1 = file.getName();
                        str_filename2 = str_filename1.substring(0, str_filename1.lastIndexOf("."));
                        textReader();
                    }
                    else{
                        str_pkg = file.getAbsolutePath();
                        textReader();
                    }
                    setVisible(false);
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    class ActionNew implements ActionListener {
        public void actionPerformed(ActionEvent e){
            dialog_frame = new DialogFrame();
        }
    }

    class ActionOpen implements ActionListener{
        public void actionPerformed(ActionEvent e){
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(),"Open");
            File file = chooser.getSelectedFile();
            try {
                str_pkg = file.getAbsoluteFile().toString();
                str_path = file.getParent();
                strPath = file.getAbsolutePath();
                str_filename1 = file.getName();
                str_filename2 = str_filename1.substring(0, str_filename1.lastIndexOf("."));
                textReader();
                System.out.println(str_path+"\\"+str_filename2);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    
    class ActionSave implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                textWriter();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    class ActionQuit implements ActionListener {
        public void actionPerformed(ActionEvent e){
            String s = texta.getText();
            if(s.equals(str_bef)){
                System.exit(0);
            }
            else{
                callQuitDialog();
            }
        }
    }
    
    public void showNextErrorLine(){
    try{
        int lineNum = regexSplitLineInt.get(cursorLineNum) - 1;
        int selectionStart = texta.getLineStartOffset(lineNum);
        int selectionEnd = texta.getLineEndOffset(lineNum);
        texta.requestFocus();
        texta.setSelectionStart(selectionStart);
        texta.setSelectionEnd(selectionEnd);
        cursorLineNum++;
        if(cursorLineNum >= regexSplitLineInt.size())
            cursorLineNum = 0;
    }
    catch (Exception e){
        e.printStackTrace();
    }
}
    public void regexSplit(String line){
    String regex = "(([a-zA-Z])\\w+.java:[0-9]+)";
    regexSplitLine.clear();
    Pattern ptn = Pattern.compile(regex);
    Matcher matcher = ptn.matcher(line);
    while(matcher.find()){
        regexSplitLine.add(matcher.group());
    }
    if(!regexSplitLine.isEmpty()){
        regexSplitLineInt.clear();
        for(String s : regexSplitLine){
            String sNum = s.substring(s.lastIndexOf(':') + 1, s.length());
            regexSplitLineInt.add(Integer.valueOf(sNum));
            System.out.println(sNum);
        }
    }
    //System.out.println(regexSplitLine);
}

    class ActionCompile implements ActionListener {
    public void actionPerformed(ActionEvent e){
        try {
        if (strPath != null && strPath != "") {
            FileOutputStream out=new FileOutputStream(new File(strPath));
            out.write(texta.getText().getBytes());
            Process proc1 = Runtime.getRuntime().exec("javac "+ strPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
            StringBuilder sb = new StringBuilder(); String line = "";
            while ((line = br.readLine()) != null){
               sb.append(line + "\n");
            }
            regexSplit(sb.toString());
            textres.setText(sb.toString());
            System.out.println(sb.toString());
        }
      }
      catch (Exception e4) {
         e4.printStackTrace();
      }
   }
}

    
    
    public static void main(String[] args) {
        new TextEditor();
    }
    
}
