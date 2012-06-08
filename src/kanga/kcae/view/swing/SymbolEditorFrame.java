package kanga.kcae.view.swing;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import kanga.kcae.object.Symbol;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

public class SymbolEditorFrame extends JFrame {
	private static final long serialVersionUID = 3386257140888958110L;

	public SymbolEditorFrame() {
        this(null);
    }

    public SymbolEditorFrame(final Symbol symbol) {
        super("Symbol Editor");
        final Container contentPane = this.getRootPane().getContentPane();
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints cpcons = new GridBagConstraints();
        final GridBagConstraints hrcons = new GridBagConstraints();
        final GridBagConstraints vrcons = new GridBagConstraints();
        
        this.setJMenuBar(this.createMenuBar());
    }

    private JMenuBar createMenuBar() {
        final JMenuBar mb = new JMenuBar();
        final JMenu mFile = new JMenu("File");
        final JMenu mTools = new JMenu("Tools");
        final JMenuItem miFileNew = new JMenuItem("New");
        final JMenuItem miFileSave = new JMenuItem("Save");
        final JMenuItem miFileSaveAs = new JMenuItem("Save As...");
        final JMenuItem miFileClose = new JMenuItem("Close");
        final JMenuItem miToolsSelect = new JMenuItem("Select");
        final JMenuItem miToolsLine = new JMenuItem("Line");

        mb.add(mFile);
        mb.add(mTools);

        mFile.add(miFileNew);
        mFile.add(miFileSave);
        mFile.add(miFileSaveAs);
        mFile.add(miFileClose);

        mTools.add(miToolsSelect);
        mTools.add(miToolsLine);

        mb.setVisible(true);

        return mb;
    }

    public static void main2(String[] args) throws IOException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("beanshell");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr, 1024);
        String line;

        System.out.print("> ");
        System.out.flush();

        while ((line = br.readLine()) != null) {
            try {
                Object result = se.eval(line);
                System.out.println(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            System.out.print("> ");
            System.out.flush();
        }
    }

    public static void main(String[] args) {
        final SymbolEditorFrame app;
        app = new SymbolEditorFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
        app.hruler.setVisible(true);
    }

    final Ruler hruler;
    final Ruler vruler;
    final SymbolViewer view;
}