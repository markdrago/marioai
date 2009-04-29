package com.mojang.mario.tools;

import com.mojang.mario.GlobalOptions;
import com.mojang.mario.MarioComponent;
import com.mojang.mario.agents.IAgent;
import com.mojang.mario.agents.RegisterableAgent;
import com.mojang.mario.agents.TCPAgent;
import com.mojang.mario.agents.human.HumanKeyboardAgent;
import com.mojang.mario.agents.ai.ForwardAgent;
import com.mojang.mario.agents.ai.RandomAgent;
import com.mojang.mario.agents.ai.MLPAgent;
import com.mojang.mario.agents.ai.ForwardJumpingAgent;
import com.mojang.mario.level.LevelGenerator;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Random;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 6:27:25 PM
 * Package: com.mojang.mario.Tools
 */
public class ToolsConfigurator extends JFrame
{
    private Evaluator evaluator;
    private static CmdLineOptions cmdLineOptions = null;

    public static void main(String[] args)
    {
        cmdLineOptions = new CmdLineOptions(args);
        // -gui on/off
        // -agent wox name, like evolvable
        // -ll digit  range [5:15], increase if succeeds.
        // -ld digit
        // -lt digit
        // -ls digit
        // -tc on/off tools
        // -gv on/off game viewer
        // -et on/off
        // -vb nothing/all/keys
        // -na digit number of attempts
        // -vis on/off
        ToolsConfigurator toolsConfigurator = null;
        toolsConfigurator = new ToolsConfigurator(null, null);
        toolsConfigurator.setVisible(cmdLineOptions.isToolsConfigurator());
        if (cmdLineOptions != null)
        {
            toolsConfigurator.ChoiceLevelType.select(cmdLineOptions.getLevelType());
            toolsConfigurator.JSpinnerLevelDifficulty.setValue(cmdLineOptions.getLevelDifficulty());
            toolsConfigurator.JSpinnerLevelRandomizationSeed.setValue(cmdLineOptions.getLevelRandSeed());
            toolsConfigurator.JSpinnerLevelLength.setValue(cmdLineOptions.getLevelLength());
            toolsConfigurator.CheckboxShowVizualization.setState(cmdLineOptions.isVisualization());
            toolsConfigurator.JSpinnerMaxAttempts.setValue(cmdLineOptions.getAttemptsNumber());
            toolsConfigurator.ChoiceAgent.select(cmdLineOptions.getAgentName());
            toolsConfigurator.CheckboxPauseWorld.setState(cmdLineOptions.isPauseWorld());
            toolsConfigurator.CheckboxPowerRestoration.setState(cmdLineOptions.isPowerRestoration());
            toolsConfigurator.CheckboxStopSimulationIfWin.setState(cmdLineOptions.isStopSimulationIfWin());
        }

        GlobalOptions.CurrentAgentStr = toolsConfigurator.ChoiceAgent.getSelectedItem();

        gameViewer = new GameViewer(null, null);

        CreateMarioComponentFrame();
        marioComponent.Init();

//        if (cmdLineOptions.isToolsConfigurator())
        toolsConfigurator.setMarioComponent(marioComponent);

        toolsConfigurator.setGameViewer(gameViewer);
        gameViewer.setAlwaysOnTop(false);
        gameViewer.setToolsConfigurator(toolsConfigurator);

        if (cmdLineOptions.isGameViewer())
        {
            gameViewer.setVisible(true);
        }

        if (!cmdLineOptions.isToolsConfigurator())
        {
            toolsConfigurator.SimulateOrPlay();
        }
    }

    private static JFrame marioComponentFrame = null;
    private static void CreateMarioComponentFrame()
    {
        if (marioComponentFrame == null)
            marioComponentFrame = new JFrame("Mario Intelligent 2.0");
        marioComponentFrame.setContentPane(marioComponent);
        marioComponentFrame.pack();
        marioComponentFrame.setResizable(false);
        marioComponentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        marioComponentFrame.setLocation(0, 0);
        marioComponentFrame.setVisible(GlobalOptions.VisualizationOn);
    }

    enum INTERFACE_TYPE {CONSOLE, GUI}

    Dimension defaultSize = new Dimension(330, 100);
    Point defaultLocation = new Point(0, 320);

    public Checkbox CheckboxShowGameViewer = new Checkbox("Show Game Viewer", true);

    public Label LabelConsole = new Label("Console:");
    public TextArea TextAreaConsole = new TextArea("Console:"/*, 8,40*/); // Verbose all, keys, events, actions, observations
    private ConsoleHistory consoleHistory;
    public Checkbox CheckboxShowVizualization = new Checkbox("Enable Visualization", GlobalOptions.VisualizationOn);
    public Checkbox CheckboxMaximizeFPS = new Checkbox("Maximize FPS");
    public Choice ChoiceAgent = new Choice();
    public Choice ChoiceLevelType = new Choice();
    public JSpinner JSpinnerLevelRandomizationSeed = new JSpinner();
    public Checkbox CheckboxEnableTimer = new Checkbox("Enable Timer", GlobalOptions.TimerOn);
    public JSpinner JSpinnerLevelDifficulty = new JSpinner();
    public Checkbox CheckboxPauseWorld = new Checkbox("Pause World");
    public Checkbox CheckboxPauseMario = new Checkbox("Pause Mario");
    public Checkbox CheckboxPowerRestoration = new Checkbox("Power Restoration");
    public JSpinner JSpinnerLevelLength = new JSpinner();
    public JSpinner JSpinnerMaxAttempts = new JSpinner();
    public Choice ChoiceVerbose = new Choice();
    private static final String strPlay        = "->  Play! ->";
    private static final String strSimulate    = "Simulate! ->";
    public Checkbox CheckboxStopSimulationIfWin = new Checkbox("Stop simulation If Win");
    public JButton JButtonPlaySimulate = new JButton(strPlay);
    public JButton JButtonResetEvaluationSummary = new JButton("Reset");

    private BasicArrowButton
            upFPS = new BasicArrowButton(BasicArrowButton.NORTH),
            downFPS = new BasicArrowButton(BasicArrowButton.SOUTH);

    // TODO            allowed time to use.
    // TODO : change agent on the fly. Artificial Contender concept? Human shows how to complete this level? Fir 13:38.
    // TODO Hot Agent PlugAndPlay.
    // TODO: cmdLineOptions : gui, agents,
// TODO: simulate until succeed.
    // TODO: time per level\    mean time per level
    // TODO: competition

    private int prevFPS = 24;

    private static GameViewer gameViewer = null; //new GameViewer(null, null);
    private static MarioComponent marioComponent = new MarioComponent(320, 240, null);

    public ToolsConfigurator(Point location, Dimension size)
    {
        super("Tools Configurator");

        setSize((size == null) ? defaultSize : size);
        setLocation((location == null) ? defaultLocation : location);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Universal Listener
        ToolsConfiguratorActions toolsConfiguratorActions = new ToolsConfiguratorActions();

        //     ToolsConfiguratorOptionsPanel
//        JPanel ToolsConfiguratorOptionsPanel = new JPanel(/*new FlowLayout()*//*GridLayout(0,2)*/);
        Container ToolsConfiguratorOptionsPanel = getContentPane();

        //        CheckboxShowGameViewer
        CheckboxShowGameViewer.addItemListener(toolsConfiguratorActions);

        //              TextFieldConsole
//        TextFieldConsole.addActionListener(toolsConfiguratorActions);

        //          CheckboxShowVizualization
        CheckboxShowVizualization.addItemListener(toolsConfiguratorActions);

        //       CheckboxMaximizeFPS
        CheckboxMaximizeFPS.addItemListener(toolsConfiguratorActions);

        //        ChoiceAgent

        ChoiceAgent.addItemListener(toolsConfiguratorActions);

        // Create an Agent here
        new ForwardAgent();
        new HumanKeyboardAgent();
        new RandomAgent();
        new ForwardJumpingAgent();
        new MLPAgent();
        new TCPAgent(4242); // TODO: Take port from CmdLineOptions;
        Set<String> AgentsNames = RegisterableAgent.getAgentsNames();
        for (String s : AgentsNames)
            ChoiceAgent.addItem(s);



        //       ChoiceLevelType
        ChoiceLevelType.addItem("Overground");
        ChoiceLevelType.addItem("Underground");
        ChoiceLevelType.addItem("Castle");
        ChoiceLevelType.addItem("Random");
        ChoiceLevelType.addItemListener(toolsConfiguratorActions);

        //      JSpinnerLevelRandomizationSeed
        JSpinnerLevelRandomizationSeed.setToolTipText("Hint: levels with same seed are identical for in observation");
        JSpinnerLevelRandomizationSeed.setValue(1);
        JSpinnerLevelRandomizationSeed.addChangeListener(toolsConfiguratorActions); //TODO : Listener;

        //  CheckboxEnableTimer
        CheckboxEnableTimer.addItemListener(toolsConfiguratorActions);
        JSpinnerLevelDifficulty.addChangeListener(toolsConfiguratorActions);

        //     CheckboxPauseWorld
        CheckboxPauseWorld.addItemListener(toolsConfiguratorActions);

        //     CheckboxPauseWorld
        CheckboxPauseMario.addItemListener(toolsConfiguratorActions);
        CheckboxPauseMario.setEnabled(false);

        //     CheckboxCheckboxPowerRestoration
        CheckboxPowerRestoration.addItemListener(toolsConfiguratorActions);
        CheckboxPowerRestoration.setEnabled(true);

        //      CheckboxStopSimulationIfWin
        CheckboxStopSimulationIfWin.addItemListener(toolsConfiguratorActions);

        //      JButtonPlaySimulate
        JButtonPlaySimulate.addActionListener(toolsConfiguratorActions);

        //      JSpinnerLevelLength
        JSpinnerLevelLength.setValue(320);
        JSpinnerLevelLength.addChangeListener(toolsConfiguratorActions);

        //      JSpinnerMaxAttempts
        JSpinnerMaxAttempts.setValue(5);
        JSpinnerMaxAttempts.addChangeListener(toolsConfiguratorActions);


        //      ChoiceVerbose
        ChoiceVerbose.addItem("Nothing");
        ChoiceVerbose.addItem("All");
        ChoiceVerbose.addItem("Keys pressed");
        ChoiceVerbose.addItem("Selected Actions");


        //      JPanel, ArrowButtons ++FPS, --FPS
        JPanel JPanelFPSFineTune = new JPanel();
        JPanelFPSFineTune.setBorder(new TitledBorder("++FPS/--FPS"));
        JPanelFPSFineTune.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");
        JPanelFPSFineTune.add(upFPS);
        JPanelFPSFineTune.add(downFPS);
        upFPS.addActionListener(toolsConfiguratorActions);
        downFPS.addActionListener(toolsConfiguratorActions);
        upFPS.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");
        downFPS.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");

        //      JPanelLevelOptions
        JPanel JPanelLevelOptions = new JPanel();
        JPanelLevelOptions.setLayout(new BoxLayout(JPanelLevelOptions, BoxLayout.Y_AXIS));
        JPanelLevelOptions.setBorder(new TitledBorder("Level Options"));

        JPanelLevelOptions.add(new Label("Level Type:"));
        JPanelLevelOptions.add(ChoiceLevelType);
        JPanelLevelOptions.add(new Label("Level Randomization Seed:"));
        JPanelLevelOptions.add(JSpinnerLevelRandomizationSeed);

        JPanelLevelOptions.add(new Label("Level Difficulty:"));
        JPanelLevelOptions.add(JSpinnerLevelDifficulty);
        JPanelLevelOptions.add(new Label("Level Length:"));
        JPanelLevelOptions.add(JSpinnerLevelLength);
        JPanelLevelOptions.add(CheckboxEnableTimer);
        JPanelLevelOptions.add(CheckboxPauseWorld);
        JPanelLevelOptions.add(CheckboxPauseMario);
        JPanelLevelOptions.add(CheckboxPowerRestoration);
        JPanelLevelOptions.add(JButtonPlaySimulate);


        JPanel JPanelMiscellaneousOptions = new JPanel();
        JPanelMiscellaneousOptions.setLayout(new BoxLayout(JPanelMiscellaneousOptions, BoxLayout.Y_AXIS));
        JPanelMiscellaneousOptions.setBorder(new TitledBorder("Miscellaneous Options"));


        JPanelMiscellaneousOptions.add(CheckboxShowGameViewer);

        JPanelMiscellaneousOptions.add(CheckboxShowVizualization);

//        JPanelMiscellaneousOptions.add(TextFieldConsole);
        JPanelMiscellaneousOptions.add(CheckboxMaximizeFPS);
        JPanelMiscellaneousOptions.add(JPanelFPSFineTune);
//        JPanelMiscellaneousOptions.add(JPanelLevelOptions);
        JPanelMiscellaneousOptions.add(new Label("Current Agent:"));
        JPanelMiscellaneousOptions.add(ChoiceAgent);
        JPanelMiscellaneousOptions.add(new Label("Verbose:"));
        JPanelMiscellaneousOptions.add(ChoiceVerbose);
        JPanelMiscellaneousOptions.add(new Label("Evaluation Summary: "));
        JPanelMiscellaneousOptions.add(JButtonResetEvaluationSummary);
        JPanelMiscellaneousOptions.add(new Label("Max # of attemps:"));
        JPanelMiscellaneousOptions.add(JSpinnerMaxAttempts);
        JPanelMiscellaneousOptions.add(CheckboxStopSimulationIfWin);

        JPanel JPanelConsole = new JPanel(new FlowLayout());
        JPanelConsole.setBorder(new TitledBorder("Console"));
        TextAreaConsole.setFont(new Font("Courier New", Font.PLAIN, 12));
        TextAreaConsole.setBackground(Color.BLACK);
        TextAreaConsole.setForeground(Color.GREEN);
        JPanelConsole.add(TextAreaConsole);

        // IF GUI
        consoleHistory = new ConsoleHistory(TextAreaConsole);

        ToolsConfiguratorOptionsPanel.add(BorderLayout.WEST, JPanelLevelOptions);
        ToolsConfiguratorOptionsPanel.add(BorderLayout.CENTER, JPanelMiscellaneousOptions);
        ToolsConfiguratorOptionsPanel.add(BorderLayout.SOUTH, JPanelConsole);

        JPanel borderPanel = new JPanel();
        borderPanel.add(BorderLayout.NORTH, ToolsConfiguratorOptionsPanel);
        setContentPane(borderPanel);
        // autosize: 
        this.pack();
    }

    public void SimulateOrPlay()
    {
        //Simulate or Play!
        EvaluatorOptions evaluatorOptions = prepareEvaluatorOptions();
        assert(evaluatorOptions != null);
        if (evaluator == null)
            evaluator = new Evaluator(evaluatorOptions);
        else
            evaluator.Init(evaluatorOptions);
        evaluator.setConsole(consoleHistory);
        evaluator.start();
        consoleHistory.addRecord("Play/Simultlation started!");
    }

    private EvaluatorOptions prepareEvaluatorOptions()
    {
        EvaluatorOptions evaluatorOptions = new EvaluatorOptions();
        evaluatorOptions.setMarioComponent(marioComponent);
        IAgent agent = RegisterableAgent.getAgentByName(ChoiceAgent.getSelectedItem());
        evaluatorOptions.setAgent(agent);
        int type = ChoiceLevelType.getSelectedIndex();
        if (type == 4)
            type = (new Random()).nextInt(4);
        evaluatorOptions.setLevelType(type);
        evaluatorOptions.setLevelDifficulty(Integer.parseInt(JSpinnerLevelDifficulty.getValue().toString()));
        evaluatorOptions.setLevelRandSeed(Integer.parseInt(JSpinnerLevelRandomizationSeed.getValue().toString()));
        evaluatorOptions.setLevelLength(Integer.parseInt(JSpinnerLevelLength.getValue().toString()));
        evaluatorOptions.setVisualization(CheckboxShowVizualization.getState());
        evaluatorOptions.maxAttempts = Integer.parseInt(JSpinnerMaxAttempts.getValue().toString());
        evaluatorOptions.setPauseWorld(CheckboxPauseWorld.getState());
        evaluatorOptions.setPowerRestoration(CheckboxPowerRestoration.getState());

        return evaluatorOptions;
    }


    public class ToolsConfiguratorActions implements ActionListener, ItemListener, ChangeListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            Object ob = ae.getSource();
            if (ob == JButtonPlaySimulate)
            {
                SimulateOrPlay();
            }
            else if (ob == upFPS)
            {
                if(++GlobalOptions.FPS >= GlobalOptions.InfiniteFPS)
                {
                    GlobalOptions.FPS = GlobalOptions.InfiniteFPS;
                    CheckboxMaximizeFPS.setState(true);
                }
                marioComponent.AdjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == downFPS)
            {
                if(--GlobalOptions.FPS < 1)
                    GlobalOptions.FPS = 1;
                CheckboxMaximizeFPS.setState(false);
                marioComponent.AdjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == JButtonResetEvaluationSummary)
            {
                evaluator = null;
            }

//            if (ob == TextFieldConsole)
//            {
//                LabelConsole.setText("TextFieldConsole sent message:");
//                gameViewer.setConsoleText(TextFieldConsole.getText());
//            }
//            else if (b.getActionCommand() == "Show")
//            {
//                iw.setVisible(true);
//                b.setLabel("Hide") ;
//            }
//            else
//            {
//                iw.setVisible(false);
//                b.setLabel("Show");
//            }
        }


        public void itemStateChanged(ItemEvent ie)
        {
            Object ob = ie.getSource();
            if (ob == CheckboxShowGameViewer)
            {
                consoleHistory.addRecord("Game Viewer " + (CheckboxShowGameViewer.getState() ? "Shown" : "Hidden") );
                gameViewer.setVisible(CheckboxShowGameViewer.getState());
            }
            else if (ob == CheckboxShowVizualization)
            {
                consoleHistory.addRecord("Vizualization " + (CheckboxShowVizualization.getState() ? "On" : "Off") );
                GlobalOptions.VisualizationOn = CheckboxShowVizualization.getState();
                marioComponentFrame.setVisible(GlobalOptions.VisualizationOn);
            }
            else if (ob == CheckboxMaximizeFPS)
            {
                prevFPS = (GlobalOptions.FPS == GlobalOptions.InfiniteFPS) ? prevFPS : GlobalOptions.FPS;
                GlobalOptions.FPS = CheckboxMaximizeFPS.getState() ? 100 : prevFPS;
                marioComponent.AdjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == CheckboxEnableTimer)
            {
                GlobalOptions.TimerOn = CheckboxEnableTimer.getState();
                consoleHistory.addRecord("Timer " + (GlobalOptions.TimerOn ? "enabled" : "disabled") );
            }
            else if (ob == CheckboxPauseWorld)
            {
                GlobalOptions.pauseWorld = CheckboxPauseWorld.getState();

                marioComponent.setPaused(GlobalOptions.pauseWorld);
                consoleHistory.addRecord("World " + (GlobalOptions.pauseWorld ? "paused" : "unpaused") );
            }
            else if (ob == CheckboxPauseMario)
            {
                TextAreaConsole.setText("1\n2\n3\n");
            }
            else if (ob == CheckboxPowerRestoration)
            {
                GlobalOptions.PowerRestoration = CheckboxPowerRestoration.getState();
                consoleHistory.addRecord("Mario Power Restoration Turned " + (GlobalOptions.PowerRestoration ? "on" : "off"));
            }
            else if (ob == CheckboxStopSimulationIfWin)
            {
                GlobalOptions.StopSimulationIfWin = CheckboxStopSimulationIfWin.getState();
                consoleHistory.addRecord("Stop simulation if Win Criteria Turned " +
                        (GlobalOptions.StopSimulationIfWin ? "on" : "off"));
            }
            else if (ob == ChoiceAgent)
            {
                consoleHistory.addRecord("Agent chosen: " + (ChoiceAgent.getSelectedItem()));
                JButtonPlaySimulate.setText(strSimulate);
//                if (ChoiceAgent.getSelectedIndex() == 0)
//                {
//                    GlobalOptions.RandomAgent = false;
//                    GlobalOptions.HumanKeyboardAgent = true;
//                    JButtonPlaySimulate.setText(strPlay);
//                }
//                else if (ChoiceAgent.getSelectedIndex() == 1)
//                {
//                    GlobalOptions.RandomAgent = true;
//                    GlobalOptions.HumanKeyboardAgent = false;
//                }
                GlobalOptions.CurrentAgentStr = ChoiceAgent.getSelectedItem();
            }
            else if (ob == ChoiceLevelType)
            {

            }
            else if (ob == ChoiceVerbose)
            {

            }
        }

        public void stateChanged(ChangeEvent changeEvent)
        {
            Object ob = changeEvent.getSource();
            if (ob == JSpinnerLevelRandomizationSeed)
            {

                //Change random seed in Evaluator/ Simulator Options
            }
            else if (ob == JSpinnerLevelDifficulty)
            {

            }
            else if (ob == JSpinnerLevelLength)
            {
                if (Integer.parseInt(JSpinnerLevelLength.getValue().toString()) < LevelGenerator.LevelLengthMinThreshold)
                    JSpinnerLevelLength.setValue(LevelGenerator.LevelLengthMinThreshold);
            }
        }
    }

    public void setGameViewer(GameViewer gameViewer) {        this.gameViewer = gameViewer;    }
    public void setMarioComponent(MarioComponent marioComponent)
    {
        this.marioComponent = marioComponent;
        this.marioComponent.setGameViewer(gameViewer);
    }
    public MarioComponent getMarioComponent() {          return marioComponent;    }

    public void setConsoleText(String text)
    {
        LabelConsole.setText("Console got message:");
        consoleHistory.addRecord("\nConsole got message:\n" + text);
//        TextFieldConsole.setText(text);
    }
}
 class ConsoleHistory
{
    TextArea textAreaConsole = null;

    public ConsoleHistory(TextArea textAreaConsole)
    {
        this.textAreaConsole = textAreaConsole;
    }

    private String history = "console:";
    public void addRecord(String record)
    {
        history += "\n" + record;
        if (textAreaConsole != null)
            textAreaConsole.setText(history);
        System.out.println(record);
    }
    public String getHistory() {            return history;        }
}
