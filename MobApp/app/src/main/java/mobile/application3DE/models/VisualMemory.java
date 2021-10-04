package mobile.application3DE.models;

public class VisualMemory {

    String SimilarityScore,DemantiaRiskLevel;

    public VisualMemory() {
    }

    public VisualMemory(String similarityScore, String demantiaRiskLevel) {
        SimilarityScore = similarityScore;
        DemantiaRiskLevel = demantiaRiskLevel;
    }


    public String getSimilarityScore() {
        return SimilarityScore;
    }

    public void setSimilarityScore(String similarityScore) {
        SimilarityScore = similarityScore;
    }

    public String getDemantiaRiskLevel() {
        return DemantiaRiskLevel;
    }

    public void setDemantiaRiskLevel(String demantiaRiskLevel) {
        DemantiaRiskLevel = demantiaRiskLevel;
    }
}
