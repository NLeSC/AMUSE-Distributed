package nl.esciencecenter.amuse.distributed.jobs;

public class FunctionJobDescription extends AmuseJobDescription {
    
    private static final long serialVersionUID = 1L;
    
    private final String function;
    private final String arguments;

    public FunctionJobDescription(String function, String arguments, String stdoutFile, String stderrFile, String nodeLabel) {
        super(stdoutFile, stderrFile, nodeLabel);
        
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public int getNrOfSlots() {
        return 1;
    }

    @Override
    public String getType() {
        return "function";
    }
    
    public String getFunction() {
        return function;
    }
    
    public String getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "FunctionJobDescription [id=" + id + ", stdoutFile=" + stdoutFile + ", stderrFile=" + stderrFile + ", label="
                + label + "]";
    }

}
