package br.izaias.valentim.msimprovements.entities.messageModel;

public class ImprovementToSendStatus {
    private String name;
    private String description;
    private String result;
    private int approved;
    private int rejected;

    public ImprovementToSendStatus(String name, String description, String result, int approved, int rejected) {
        this.name = name;
        this.description = description;
        this.result = result;
        this.approved = approved;
        this.rejected = rejected;
    }

    public ImprovementToSendStatus() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }
}
