package com;

public class Procedure {
    private String name;        // 进程名称
    private int startTime;      // 开始时间
    private int conTime;        // 持续时间
    private int runTime;        // 已运行时间
    private int priority;       // 优先级
    private State curState;     // 当前状态
    private int startAddr;      // 内存起始地址
    private int memory;         // 占用内存的大小

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getConTime() {
        return conTime;
    }

    public void setConTime(int conTime) {
        this.conTime = conTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getCurState() {
        return curState;
    }

    public void setCurState(State curState) {
        this.curState = curState;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int startAddr) {
        this.startAddr = startAddr;
    }

    @Override
    public String toString() {
        String str = "";
        str = str + "进程名称: " + this.name + "    "
                + "已运行时间: " + this.runTime + "    "
                + "总运行时间: " + this.conTime + "    "
                + "到达时间: " + this.startTime + "    "
                + "进程状态: " + State.getState(this.curState) + "   "
                + "优先级: " + this.priority + "   "
                + "内存大小: " + this.memory + "KB    ";

        return str;
    }

    public Procedure() {

    }

    public Procedure(String name, int startTime, int conTime, int priority, int memory) {
        this.name = name;
        this.startTime = startTime;
        this.conTime = conTime;
        this.priority = priority;
        this.memory = memory;
        this.curState = State.READY;
    }
}
