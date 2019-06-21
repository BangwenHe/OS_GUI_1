import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import com.Procedure;
import com.State;

public class MainFrame {
    private JTextField textField2;
    private JTextField textField3;
    private JButton comfirmButton;
    private JButton clearButton;
    private JTextField textField4;
    private JButton blockButton;
    private JButton callButton;
    private JButton startButton;
    private JComboBox comboBox1;
    private JButton killButton;
    private JTextArea pTextArea;
    private JPanel mainPanel;
    private JTextField textField1;
    private JList list1;
    private JTextField textField5;
    private JList list2;
    private JComboBox comboBox2;
    private DefaultListModel<Procedure> procedureList;
    private DefaultListModel<Procedure> memoryList;

    private boolean isBlocked = false;                 // 表示是否阻塞
    private boolean isKilled = false;                  // 表示是否被终止
    private boolean isRunning = false;                 // 表示CPU中是否还有进程在运行
    private final int m = 1024;                        // 设置内存空间的最大值
    // 用以构造优先队列
    private Comparator<Procedure> cmp = new Comparator<Procedure>() {
        @Override
        public int compare(Procedure o1, Procedure o2) {
            if (o1.getPriority() != o2.getPriority()) {
                return o1.getPriority() - o2.getPriority();
            } else {
                return o1.getStartTime() - o2.getStartTime();
            }
        }
    };
    // 维护一个就绪队列
    private PriorityQueue<Procedure> readyQueue = new PriorityQueue<Procedure>(5, cmp);
    // 维护一个阻塞队列
    private Queue<Procedure> blockQueue = new LinkedList<>();

    private void FCFSAlgorithm() {
        System.out.println("FCFS先进先服务算法开始执行");
        threadSchedule t = new threadSchedule();
        t.start();
    }

    private void refreshList(Procedure p, State pre, State cur) {
        int index = procedureList.indexOf(p);

        if (pre == State.READY && cur == State.RUN) {
            p.setCurState(State.RUN);
        } else if (pre == State.RUN && cur == State.RUN) {
            p.setRunTime(p.getRunTime() + 1);
        } else if (pre == State.RUN && cur == State.BLOCK) {
            p.setCurState(State.BLOCK);
        } else if (pre == State.RUN && cur == State.READY) {
            p.setRunTime(p.getRunTime() + 1);
            procedureList.removeElementAt(index);

            memoryList.removeElementAt(memoryList.indexOf(p));
            list2.setListData(memoryList.toArray());
            list2.repaint();
        } else if (pre == State.BLOCK && cur == State.READY) {
            p.setCurState(State.READY);
        } else {
            System.out.println("错误");
            pTextArea.append("错误的进程调度!\n");
        }

        list1.setModel(procedureList);
        list1.repaint();                            // 保证刷新时list可以刷新

        System.out.println("线程状态列表已刷新");
    }

    private void refreshText(Procedure p, State pre, State cur) {

        if (pre == State.READY && cur == State.RUN) {
            System.out.println("就绪状态->执行状态");
            pTextArea.append("进程\"" + p.getName() + "\"开始占用CPU运行!\n");
        } else if (pre == State.RUN && cur == State.RUN) {
            System.out.println("执行状态->执行状态");
            pTextArea.append("进程" + p.getName() + "已运行时间: " + (p.getRunTime() - 1) + '/' + p.getConTime() + "\n");
        } else if (pre == State.RUN && cur == State.BLOCK) {
            System.out.println("执行状态->阻塞状态");
            pTextArea.append("进程" + p.getName() + "进入阻塞状态!\n");
        } else if (pre == State.RUN && cur == State.READY) {
            System.out.println("执行状态->*");
            pTextArea.append("进程" + p.getName() + "已运行时间: " + (p.getRunTime() - 1) + '/' + p.getConTime() + "\n");
            pTextArea.append("进程" + p.getName() + "运行已结束!\n");
        } else if (pre == State.BLOCK && cur == State.READY) {
            System.out.println("阻塞状态->就绪状态");
            pTextArea.append("进程" + p.getName() + "进入就绪队列!\n");
        } else {
            System.out.println("错误");
            pTextArea.append("错误的进程调度!\n");
        }

        pTextArea.setCaretPosition(pTextArea.getDocument().getLength());
        System.out.println("线程状态输出文本框已刷新");
    }

    private MainFrame() {
        procedureList = new DefaultListModel<>();
        memoryList = new DefaultListModel<>() {
            @Override
            public Object[] toArray() {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < this.getSize(); i++) {
                    String str = this.get(i).getStartAddr() + "-" + (this.get(i).getStartAddr() + this.get(i).getMemory()) + "KB";
                    arrayList.add(str + "    " + this.get(i).getName());
                }

                return arrayList.toArray();
            }
        };

        comfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = textField1.getText();
                } catch (NullPointerException ne) {
                    JOptionPane.showMessageDialog(null, "进程名不能为空");
                    textField1.setText("");
                }

                String name = textField1.getText();
                String arrivalTimeStr = textField2.getText();
                String runTimeStr = textField3.getText();
                String priorityStr = textField4.getText();
                String memoryStr = textField5.getText();

                try {
                    int arrivalTime = Integer.parseInt(arrivalTimeStr);
                    int runTime = Integer.parseInt(runTimeStr);
                    int priority = Integer.parseInt(priorityStr);
                    int memory = Integer.parseInt(memoryStr);

                    if (memory > m) {
                        JOptionPane.showMessageDialog(null, "进程内存超过最大内存");
                        return;
                    }

                    Procedure p = new Procedure(name, arrivalTime, runTime, priority, memory);
                    p.setCurState(State.READY);
                    procedureList.addElement(p);
                    // todo 修改添加元素
                    memoryAllocate(p);
                    list1.setModel(procedureList);
                    list2.setListData(memoryList.toArray());

                    if (isRunning) {
                        readyQueue.add(p);
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "时间, 优先级以及内存大小必须为整数");
                    textField2.setText("");
                    textField3.setText("");
                    textField4.setText("");
                }

                textField1.setText("");
                textField2.setText("");
                textField3.setText("");
                textField4.setText("");
                textField5.setText("");
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setText("");
                textField2.setText("");
                textField3.setText("");
                textField4.setText("");
                textField5.setText("");
            }
        });

        blockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlocked = true;
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readyQueue.clear();
                for (int i = 0; i < procedureList.getSize(); i++) {
                    System.out.println(i);
                    readyQueue.add(procedureList.elementAt(i));
                }

                FCFSAlgorithm();
            }
        });

        callButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!blockQueue.isEmpty()) {
                    refreshList(blockQueue.peek(), State.BLOCK, State.READY);
                    refreshText(blockQueue.peek(), State.BLOCK, State.READY);
                    readyQueue.add(blockQueue.poll());

                    if (!isRunning) {
                        FCFSAlgorithm();
                    }
                } else {
                    pTextArea.append("阻塞队列中无阻塞进程!\n");
                    pTextArea.setCaretPosition(pTextArea.getDocument().getLength());
                }
            }
        });

        mainPanel.setPreferredSize(new Dimension(1130, 500));

        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isKilled = true;
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainFrame");
        frame.setContentPane(new MainFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    class threadSchedule extends Thread {
        @Override
        public void run() {
            pTextArea.append("FCFS算法开始执行: \n");
            pTextArea.setCaretPosition(pTextArea.getDocument().getLength());

            // 进程的状态在refreshList中更新
            while (!readyQueue.isEmpty()) {
                isBlocked = false;
                isKilled = false;
                isRunning = true;
                Procedure p = readyQueue.poll();
                System.out.println("开始调度");
                refreshList(p, com.State.READY, com.State.RUN);
                refreshText(p, com.State.READY, com.State.RUN);

                while (!isBlocked && !isKilled) {
                    try {
                        refreshList(p, com.State.RUN, com.State.RUN);
                        refreshText(p, com.State.RUN, com.State.RUN);
                        Thread.currentThread().sleep(3000);

                        if (p.getRunTime() == p.getConTime())
                            break;

                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }


                if (isBlocked && p.getRunTime() < p.getConTime()) {
                    System.out.println("中断开始, 线程进入阻塞状态!");
                    refreshList(p, com.State.RUN, com.State.BLOCK);
                    refreshText(p, com.State.RUN, com.State.BLOCK);
                    blockQueue.add(p);
                } else {
                    System.out.println("进程已结束!");

                    refreshList(p, com.State.RUN, com.State.READY);
                    refreshText(p, com.State.RUN, com.State.READY);

                    if (!readyQueue.isEmpty()) {
                        pTextArea.append("CPU调度中...\n");
                        pTextArea.setCaretPosition(pTextArea.getDocument().getLength());
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                }
            }

            isRunning = false;
            System.out.println("所有进程已执行完毕!" + "阻塞队列剩余: " + blockQueue.size() + "个!");
            pTextArea.append("所有进程已执行完毕!" + "阻塞队列剩余: " + blockQueue.size() + "个!\n");
            pTextArea.setCaretPosition(pTextArea.getDocument().getLength());
        }
    }

    private void memoryAllocate(Procedure p) {
        int lastEnd;
        int curBegin;
        boolean isAllocated = false;

        if (memoryList.getSize() == 0) {
            p.setStartAddr(0);
            memoryList.addElement(p);
            isAllocated = true;
        } else {
            for (int i = 0; i < memoryList.getSize() + 1; i++) {
                if (i == 0) {
                    lastEnd = 0;
                } else {
                    lastEnd = memoryList.get(i - 1).getStartAddr() + memoryList.get(i - 1).getMemory();
                }

                if (i < memoryList.getSize()) {
                    curBegin = memoryList.get(i).getStartAddr();
                } else {
                    curBegin = m;
                }

                if (curBegin - lastEnd >= p.getMemory()) {
                    p.setStartAddr(lastEnd);
                    memoryList.insertElementAt(p, i);
                    isAllocated = true;
                    break;
                }
            }
        }

        if (!isAllocated) {
            System.out.println("内存未分配成功!");
            JOptionPane.showMessageDialog(null, "进程所需内存过大, 无法分配内存!");
        }

        System.out.println(memoryList.getSize());
    }
}
