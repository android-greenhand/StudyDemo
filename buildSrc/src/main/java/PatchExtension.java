/**
 *
 */
public class PatchExtension {

    boolean debugOn;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    String output;
    String applicationName;

    public PatchExtension(boolean debugOn, String output, String applicationName) {
        this.debugOn = debugOn;
        this.output = output;
        this.applicationName = applicationName;
    }

    public PatchExtension() {
    }

    @Override
    public String toString() {
        return "PatchExtension{" +
                "debugOn=" + debugOn +
                ", output='" + output + '\'' +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
