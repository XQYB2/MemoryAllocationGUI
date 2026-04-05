/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package memoryallocationgui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author RONIESE MAMAED
 */
public class MemoryAllocation extends JFrame {

    // Job List Variables
    private ArrayList<Integer> jobList = new ArrayList<>();
    private ArrayList<Integer> requestList = new ArrayList<>();

    // Memory List Variables
    private ArrayList<Integer> memoryLocation = new ArrayList<>();
    private ArrayList<Integer> memoryBlock = new ArrayList<>();
    private ArrayList<Integer> jobNumberML = new ArrayList<>();
    private ArrayList<Integer> jobSize = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<Integer> internalFragmentation = new ArrayList<>();

    // GUI Components
    private JTable memoryTable;
    private DefaultTableModel memoryTableModel;
    private JTable jobTable;
    private DefaultTableModel jobTableModel;
    private JTextArea resultArea;
    private JComboBox<String> algorithmCombo;

    public MemoryAllocation() {
        setTitle("Memory Allocation Simulator");
        setSize(1000, 700);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                
                JOptionPane.showMessageDialog(MemoryAllocation.this,
                        "Goodbye! Thank you for using the Memory Allocation Simulator.",
                        "Exiting",
                        JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });
        
        setLayout(new BorderLayout(10, 10));

        initComponents();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // Top Panel - Algorithm Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Algorithm"));

        JLabel algoLabel = new JLabel("Algorithm:");
        String[] algorithms = {"First Fit", "Best Fit"};
        algorithmCombo = new JComboBox<>(algorithms);

        JButton allocateBtn = new JButton("Allocate Memory");
        allocateBtn.addActionListener(e -> allocateMemory());

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> resetAll());

        topPanel.add(algoLabel);
        topPanel.add(algorithmCombo);
        topPanel.add(allocateBtn);
        topPanel.add(resetBtn);

        // Center Panel - Split into two sections
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setDividerLocation(450);

        // Left Side - Memory Blocks
        JPanel memoryPanel = new JPanel(new BorderLayout(5, 5));
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memory Blocks"));

        String[] memoryColumns = {"Location", "Block Size", "Job#", "Job Size", "Status", "Frag."};
        memoryTableModel = new DefaultTableModel(memoryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memoryTable = new JTable(memoryTableModel);
        JScrollPane memoryScroll = new JScrollPane(memoryTable);

        JPanel memoryInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel blockLabel = new JLabel("Block Size:");
        JTextField blockField = new JTextField(10);
        JButton addBlockBtn = new JButton("Add Block");

        addBlockBtn.addActionListener(e -> {
            try {
                int size = Integer.parseInt(blockField.getText().trim());
                if (size > 0) {
                    addMemoryBlock(size);
                    blockField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Block size must be positive!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            }
        });

        memoryInputPanel.add(blockLabel);
        memoryInputPanel.add(blockField);
        memoryInputPanel.add(addBlockBtn);

        memoryPanel.add(memoryScroll, BorderLayout.CENTER);
        memoryPanel.add(memoryInputPanel, BorderLayout.SOUTH);

        // Right Side - Jobs
        JPanel jobPanel = new JPanel(new BorderLayout(5, 5));
        jobPanel.setBorder(BorderFactory.createTitledBorder("Jobs"));

        String[] jobColumns = {"Job#", "Memory Requested"};
        jobTableModel = new DefaultTableModel(jobColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobTable = new JTable(jobTableModel);
        JScrollPane jobScroll = new JScrollPane(jobTable);

        JPanel jobInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jobLabel = new JLabel("Memory Request:");
        JTextField jobField = new JTextField(10);
        JButton addJobBtn = new JButton("Add Job");

        addJobBtn.addActionListener(e -> {
            try {
                int memory = Integer.parseInt(jobField.getText().trim());
                if (memory > 0) {
                    addJob(memory);
                    jobField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Memory request must be positive!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            }
        });

        jobInputPanel.add(jobLabel);
        jobInputPanel.add(jobField);
        jobInputPanel.add(addJobBtn);

        jobPanel.add(jobScroll, BorderLayout.CENTER);
        jobPanel.add(jobInputPanel, BorderLayout.SOUTH);

        centerSplit.setLeftComponent(memoryPanel);
        centerSplit.setRightComponent(jobPanel);

        // Bottom Panel - Results
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Allocation Results"));

        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);

        bottomPanel.add(resultScroll, BorderLayout.CENTER);

        // Add all panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerSplit, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addMemoryBlock(int size) {
        int location = memoryLocation.size();
        memoryLocation.add(location);
        memoryBlock.add(size);
        jobNumberML.add(-1);
        jobSize.add(0);
        status.add("free");
        internalFragmentation.add(0);

        memoryTableModel.addRow(new Object[]{location, size, "-", "-", "free", 0});
    }

    private void addJob(int memory) {
        int jobNum = jobList.size() + 1;
        jobList.add(jobNum);
        requestList.add(memory);

        jobTableModel.addRow(new Object[]{jobNum, memory});
    }

    private void allocateMemory() {
        if (memoryBlock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add memory blocks first!");
            return;
        }

        if (requestList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add jobs first!");
            return;
        }

        String algorithm = (String) algorithmCombo.getSelectedItem();

        switch (algorithm) {
            case "First Fit":
                firstFit();
                break;
            case "Best Fit":
                bestFit();
                break;
        }
    }

    private void firstFit() {
        resetMemoryBlocks();
        resultArea.setText("=== FIRST FIT ALGORITHM ===\n\n");

        int allocatedCount = 0;

        for (int i = 0; i < jobList.size(); i++) {
            int jobNum = jobList.get(i);
            int memNeeded = requestList.get(i);
            boolean allocated = false;

            // Find first block that fits
            for (int j = 0; j < memoryBlock.size(); j++) {
                if (status.get(j).equals("free") && memoryBlock.get(j) >= memNeeded) {
                    // Allocate job to this block
                    jobNumberML.set(j, jobNum);
                    jobSize.set(j, memNeeded);
                    status.set(j, "busy");
                    internalFragmentation.set(j, memoryBlock.get(j) - memNeeded);

                    updateMemoryTable(j);

                    resultArea.append(String.format("Job %d (size: %d) allocated to Block %d (size: %d)\n",
                            jobNum, memNeeded, j, memoryBlock.get(j)));
                    resultArea.append(String.format("  Internal Fragmentation: %d\n\n",
                            internalFragmentation.get(j)));

                    allocated = true;
                    allocatedCount++;
                    break;
                }
            }

            if (!allocated) {
                resultArea.append(String.format("Job %d (size: %d) - NO SUITABLE BLOCK FOUND!\n\n",
                        jobNum, memNeeded));
            }
        }

        displaySummary(allocatedCount);
    }

    private void bestFit() {
        resetMemoryBlocks();
        resultArea.setText("=== BEST FIT ALGORITHM ===\n\n");

        int allocatedCount = 0;

        for (int i = 0; i < jobList.size(); i++) {
            int jobNum = jobList.get(i);
            int memNeeded = requestList.get(i);

            // Find best fitting block (smallest block that fits)
            int bestIdx = -1;
            int minDiff = Integer.MAX_VALUE;

            for (int j = 0; j < memoryBlock.size(); j++) {
                if (status.get(j).equals("free") && memoryBlock.get(j) >= memNeeded) {
                    int diff = memoryBlock.get(j) - memNeeded;
                    if (diff < minDiff) {
                        minDiff = diff;
                        bestIdx = j;
                    }
                }
            }

            if (bestIdx != -1) {
                // Allocate job to best block
                jobNumberML.set(bestIdx, jobNum);
                jobSize.set(bestIdx, memNeeded);
                status.set(bestIdx, "busy");
                internalFragmentation.set(bestIdx, memoryBlock.get(bestIdx) - memNeeded);

                updateMemoryTable(bestIdx);

                resultArea.append(String.format("Job %d (size: %d) allocated to Block %d (size: %d)\n",
                        jobNum, memNeeded, bestIdx, memoryBlock.get(bestIdx)));
                resultArea.append(String.format("  Internal Fragmentation: %d\n\n",
                        internalFragmentation.get(bestIdx)));

                allocatedCount++;
            } else {
                resultArea.append(String.format("Job %d (size: %d) - NO SUITABLE BLOCK FOUND!\n\n",
                        jobNum, memNeeded));
            }
        }

        displaySummary(allocatedCount);
    }

    private void resetMemoryBlocks() {
        for (int i = 0; i < memoryBlock.size(); i++) {
            jobNumberML.set(i, -1);
            jobSize.set(i, 0);
            status.set(i, "free");
            internalFragmentation.set(i, 0);
            updateMemoryTable(i);
        }
    }

    private void updateMemoryTable(int row) {
        memoryTableModel.setValueAt(memoryLocation.get(row), row, 0);
        memoryTableModel.setValueAt(memoryBlock.get(row), row, 1);
        memoryTableModel.setValueAt(jobNumberML.get(row) == -1 ? "-" : jobNumberML.get(row), row, 2);
        memoryTableModel.setValueAt(jobSize.get(row) == 0 ? "-" : jobSize.get(row), row, 3);
        memoryTableModel.setValueAt(status.get(row), row, 4);
        memoryTableModel.setValueAt(internalFragmentation.get(row), row, 5);
    }

    private void displaySummary(int allocatedCount) {
        resultArea.append("\n========================================\n");
        resultArea.append("SUMMARY\n");
        resultArea.append("========================================\n");
        resultArea.append(String.format("Total Jobs: %d\n", jobList.size()));
        resultArea.append(String.format("Allocated: %d\n", allocatedCount));
        resultArea.append(String.format("Not Allocated: %d\n", jobList.size() - allocatedCount));

        int totalFragmentation = 0;
        int totalAvailable = 0;
        int totalUsed = 0;

        for (int frag : internalFragmentation) {
            totalFragmentation += frag;
        }
        
        for (int blockSize : memoryBlock) {
            totalAvailable += blockSize;
        }
        
        for (int allocatedSize : jobSize) {
            totalUsed += allocatedSize;
        }

        resultArea.append(String.format("Total Internal Fragmentation: %d\n", totalFragmentation));
        
        resultArea.append("----------------------------------------\n");
        resultArea.append(String.format("Total Available Memory: %d\n", totalAvailable));
        resultArea.append(String.format("Total Used Memory: %d\n", totalUsed));
    }

    private void resetAll() {
        memoryLocation.clear();
        memoryBlock.clear();
        jobNumberML.clear();
        jobSize.clear();
        status.clear();
        internalFragmentation.clear();
        jobList.clear();
        requestList.clear();

        memoryTableModel.setRowCount(0);
        jobTableModel.setRowCount(0);
        resultArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MemoryAllocation();
        });
    }
}