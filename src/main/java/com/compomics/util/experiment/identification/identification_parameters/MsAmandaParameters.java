package com.compomics.util.experiment.identification.identification_parameters;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.IdentificationAlgorithmParameter;

/**
 * The MS Amanda specific parameters.
 *
 * @author Harald Barsnes
 */
public class MsAmandaParameters implements IdentificationAlgorithmParameter {

    /**
     * Version number for deserialization.
     */
    static final long serialVersionUID = -8458620189315975268L;
    /**
     * Maximal e-value cut-off.
     */
    private Double maxEValue = 100.0;
    /**
     * Defines whether a decoy database shall be created and searched against.
     * Decoy FASTS files are generated by reverting protein sequences,
     * accessions are marked with the prefix “REV_”.
     */
    private boolean generateDecoy = false;
    /**
     * The MS Amanda instrument ID.
     */
    private String instrumentID = "b, y";
    /**
     * The maximum rank.
     */
    private Integer maxRank = 5; // @TODO: find optimal default value!
    /**
     * Defines whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     */
    private boolean monoisotopic = true;

    /**
     * Constructor.
     */
    public MsAmandaParameters() {
    }

    @Override
    public Advocate getAlgorithm() {
        return Advocate.msAmanda;
    }

    @Override
    public boolean equals(IdentificationAlgorithmParameter identificationAlgorithmParameter) {

        if (identificationAlgorithmParameter instanceof MsAmandaParameters) {
            MsAmandaParameters msAmandaParameters = (MsAmandaParameters) identificationAlgorithmParameter;
            if (generateDecoy != msAmandaParameters.generateDecoy()) {
                return false;
            }
            if (monoisotopic != msAmandaParameters.isMonoIsotopic()) {
                return false;
            }
            if (!instrumentID.equalsIgnoreCase(msAmandaParameters.getInstrumentID())) {
                return false;
            }
            if (maxRank != msAmandaParameters.getMaxRank()) {
                return false;
            }
            double diff = Math.abs(maxEValue - msAmandaParameters.getMaxEValue());
            if (diff > 0.0000000000001) {
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public String toString(boolean html) {
        String newLine = System.getProperty("line.separator");

        if (html) {
            newLine = "<br>";
        }

        StringBuilder output = new StringBuilder();
        Advocate advocate = getAlgorithm();
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append("# ").append(advocate.getName()).append(" Specific Parameters");
        output.append(newLine);
        output.append("# ------------------------------------------------------------------");
        output.append(newLine);
        output.append(newLine);

        output.append("SEARCH_DECOY=");
        output.append(generateDecoy);
        output.append(newLine);
        output.append("INSTRUMENT_ID=");
        output.append(instrumentID);
        output.append(newLine);
        output.append("MONOISOTOPIC=");
        output.append(monoisotopic);
        output.append(newLine);
        output.append("MAX_RANK=");
        output.append(maxRank);
        output.append(newLine);
        output.append("EVALUE_CUTOFF=");
        output.append(maxEValue);
        output.append(newLine);

        return output.toString();
    }

    /**
     * Returns whether a decoy database shall be created and searched against.
     *
     * @return whether a decoy database shall be created and searched against
     */
    public boolean generateDecoy() {
        return generateDecoy;
    }

    /**
     * Set whether a decoy database shall be created and searched against.
     *
     * @param generateDecoy the generateDecoy to set
     */
    public void setGenerateDecoyDatabase(boolean generateDecoy) {
        this.generateDecoy = generateDecoy;
    }

    /**
     * Returns whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @return monoisotopic mass values shall be used (in contrast to average
     * mass values)
     */
    public boolean isMonoIsotopic() {
        return monoisotopic;
    }

    /**
     * Set whether monoisotopic mass values shall be used (in contrast to
     * average mass values).
     *
     * @param monoisotopic the monoisotopic to set
     */
    public void setMonoIsotopic(boolean monoisotopic) {
        this.monoisotopic = monoisotopic;
    }

    /**
     * Return the instrument ID.
     *
     * @return the instrumentID
     */
    public String getInstrumentID() {
        return instrumentID;
    }

    /**
     * Set the instrument ID.
     *
     * @param instrumentID the instrumentID to set
     */
    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    /**
     * Returns the maximum rank.
     *
     * @return the maxRank
     */
    public Integer getMaxRank() {
        return maxRank;
    }

    /**
     * Set the maximum rank.
     *
     * @param maxRank the maxRank to set
     */
    public void setMaxRank(Integer maxRank) {
        this.maxRank = maxRank;
    }

    /**
     * Returns the maximal e-value searched for.
     *
     * @return the maximal e-value searched for
     */
    public Double getMaxEValue() {
        return maxEValue;
    }

    /**
     * Sets the maximal e-value searched for.
     *
     * @param maxEValue the maximal e-value searched for
     */
    public void setMaxEValue(Double maxEValue) {
        this.maxEValue = maxEValue;
    }
}
