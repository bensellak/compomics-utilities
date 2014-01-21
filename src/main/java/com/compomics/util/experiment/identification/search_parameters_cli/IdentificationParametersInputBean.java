package com.compomics.util.experiment.identification.search_parameters_cli;

import com.compomics.software.CommandLineUtils;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.identification_parameters.OmssaParameters;
import com.compomics.util.experiment.identification.identification_parameters.PepnovoParameters;
import com.compomics.util.experiment.identification.identification_parameters.XtandemParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.preferences.ModificationProfile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;

/**
 * This class contains the parses parameters from a command line and stores them
 * in a SearchParameters object.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class IdentificationParametersInputBean {

    /**
     * The search parameters.
     */
    private SearchParameters searchParameters;
    /**
     * The file where to save the parameters
     */
    private File destinationFile;
    /**
     * If true the modifications will be listed on the screen
     */
    private Boolean listMods = false;
    /**
     * The compomics PTM factory.
     */
    private PTMFactory ptmFactory = PTMFactory.getInstance();
    /**
     * The enzyme factory.
     */
    private EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();

    /**
     * Takes all the arguments from a command line.
     *
     * @param aLine the command line
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public IdentificationParametersInputBean(CommandLine aLine) throws FileNotFoundException, IOException, ClassNotFoundException {
        
        if (aLine.hasOption(IdentificationParametersCLIParams.MODS.id)) {
            listMods = true;
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OUTPUT.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OUTPUT.id);
            if (!arg.endsWith(".parameters")) {
                arg+= ".parameters";
            }
            destinationFile = new File(arg);
        }
        
        searchParameters = new SearchParameters();

        if (aLine.hasOption(IdentificationParametersCLIParams.PREC_PPM.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PREC_PPM.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                searchParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.PPM);
            } else {
                searchParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FRAG_PPM.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FRAG_PPM.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                searchParameters.setFragmentAccuracyType(SearchParameters.MassAccuracyType.PPM);
            } else {
                searchParameters.setFragmentAccuracyType(SearchParameters.MassAccuracyType.DA);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PREC_TOL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PREC_TOL.id);
            Double option = new Double(arg);
            searchParameters.setPrecursorAccuracy(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FRAG_TOL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FRAG_TOL.id);
            Double option = new Double(arg);
            searchParameters.setFragmentIonAccuracy(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.ENZYME.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.ENZYME.id);
            Enzyme option = enzymeFactory.getEnzyme(arg);
            searchParameters.setEnzyme(option);
        } else {
            Enzyme option = enzymeFactory.getEnzyme("Trypsin"); // no enzyme given, default to Trypsin
            searchParameters.setEnzyme(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.DB.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.DB.id);
            File fastaFile = new File(arg);
            searchParameters.setFastaFile(fastaFile);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.MC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MC.id);
            Integer option = new Integer(arg);
            searchParameters.setnMissedCleavages(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FI.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FI.id);
            searchParameters.setIonSearched1(arg);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.RI.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.RI.id);
            searchParameters.setIonSearched2(arg);
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.MIN_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MIN_CHARGE.id);
            Integer option = new Integer(arg);
            searchParameters.setMinChargeSearched(new Charge(Charge.PLUS, option));
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.MAX_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MAX_CHARGE.id);
            Integer option = new Integer(arg);
            searchParameters.setMinChargeSearched(new Charge(Charge.PLUS, option));
        }

        ModificationProfile modificationProfile = new ModificationProfile();
        if (aLine.hasOption(IdentificationParametersCLIParams.FIXED_MODS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FIXED_MODS.id);
            ArrayList<String> args = CommandLineUtils.splitInput(arg);
            for (String ptmName : args) {
                PTM modification = ptmFactory.getPTM(ptmName);
                modificationProfile.addFixedModification(modification);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.VARIABLE_MODS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.VARIABLE_MODS.id);
            ArrayList<String> args = CommandLineUtils.splitInput(arg);
            for (String ptmName : args) {
                PTM modification = ptmFactory.getPTM(ptmName);
                modificationProfile.addVariableModification(modification);
            }
        }
        searchParameters.setModificationProfile(modificationProfile);

        OmssaParameters omssaParameters = new OmssaParameters();
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_REMOVE_PREC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_REMOVE_PREC.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                omssaParameters.setRemovePrecursor(true);
            } else {
                omssaParameters.setRemovePrecursor(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_SCALE_PREC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_SCALE_PREC.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                omssaParameters.setScalePrecursor(true);
            } else {
                omssaParameters.setScalePrecursor(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_ESTIMATE_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_ESTIMATE_CHARGE.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                omssaParameters.setEstimateCharge(true);
            } else {
                omssaParameters.setEstimateCharge(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MAX_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MAX_EVALUE.id);
            Double option = new Double(arg);
            omssaParameters.setMaxEValue(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_HITLIST_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_HITLIST_LENGTH.id);
            Integer option = new Integer(arg);
            omssaParameters.setHitListLength(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MIN_PEP_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MIN_PEP_LENGTH.id);
            Integer option = new Integer(arg);
            omssaParameters.setMinPeptideLength(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MAX_PEP_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MAX_PEP_LENGTH.id);
            Integer option = new Integer(arg);
            omssaParameters.setMaxPeptideLength(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_FORMAT.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_FORMAT.id);
            Integer option = new Integer(arg);
            omssaParameters.setSelectedOutput(OmssaParameters.getOmssaOutputTypes()[option]);
        }
        searchParameters.setIdentificationAlgorithmParameter(Advocate.OMSSA.getIndex(), omssaParameters);
        ptmFactory.setSearchedOMSSAIndexes(searchParameters.getModificationProfile());

        XtandemParameters xtandemParameters = new XtandemParameters();
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_DYNAMIC_RANGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_DYNAMIC_RANGE.id);
            Double option = new Double(arg);
            xtandemParameters.setDynamicRange(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_NPEAKS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_NPEAKS.id);
            Integer option = new Integer(arg);
            xtandemParameters.setnPeaks(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_FRAG_MZ.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_FRAG_MZ.id);
            Double option = new Double(arg);
            xtandemParameters.setMinFragmentMz(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_PEAKS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_PEAKS.id);
            Integer option = new Integer(arg);
            xtandemParameters.setMinPeaksPerSpectrum(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_NOISE_SUPPRESSION.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_NOISE_SUPPRESSION.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setUseNoiseSuppression(true);
            } else {
                xtandemParameters.setUseNoiseSuppression(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_PREC_MASS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_PREC_MASS.id);
            Double option = new Double(arg);
            xtandemParameters.setMinPrecursorMass(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_QUICK_ACETYL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_QUICK_ACETYL.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setProteinQuickAcetyl(true);
            } else {
                xtandemParameters.setProteinQuickAcetyl(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_QUICK_PYRO.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_QUICK_PYRO.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setQuickPyrolidone(true);
            } else {
                xtandemParameters.setQuickPyrolidone(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_STP_BIAS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_STP_BIAS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setStpBias(true);
            } else {
                xtandemParameters.setStpBias(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefine(true);
            } else {
                xtandemParameters.setRefine(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_EVALUE.id);
            Double option = new Double(arg);
            xtandemParameters.setMaximumExpectationValueRefinement(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefineUnanticipatedCleavages(true);
            } else {
                xtandemParameters.setRefineUnanticipatedCleavages(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SEMI.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SEMI.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefineSemi(true);
            } else {
                xtandemParameters.setRefineSemi(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setPotentialModificationsForFullRefinment(true);
            } else {
                xtandemParameters.setPotentialModificationsForFullRefinment(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_POINT_MUTATIONS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_POINT_MUTATIONS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefinePointMutations(true);
            } else {
                xtandemParameters.setRefinePointMutations(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SNAPS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SNAPS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefineSnaps(true);
            } else {
                xtandemParameters.setRefineSnaps(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SPECTRUM_SYNTHESIS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SPECTRUM_SYNTHESIS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setRefineSpectrumSynthesis(true);
            } else {
                xtandemParameters.setRefineSpectrumSynthesis(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_EVALUE.id);
            Double option = new Double(arg);
            xtandemParameters.setMaxEValue(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setOutputProteins(true);
            } else {
                xtandemParameters.setOutputProteins(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SEQUENCES.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SEQUENCES.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setOutputSequences(true);
            } else {
                xtandemParameters.setOutputSequences(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SPECTRA.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                xtandemParameters.setOutputSpectra(true);
            } else {
                xtandemParameters.setOutputSpectra(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_SKYLINE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_SKYLINE.id);
            xtandemParameters.setSkylinePath(arg);
        }
        searchParameters.setIdentificationAlgorithmParameter(Advocate.XTandem.getIndex(), xtandemParameters);

        PepnovoParameters pepnovoParameters = new PepnovoParameters();
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_HITLIST_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_HITLIST_LENGTH.id);
            Integer option = new Integer(arg);
            pepnovoParameters.setHitListLength(option);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPTNOVO_ESTIMATE_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPTNOVO_ESTIMATE_CHARGE.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                pepnovoParameters.setEstimateCharge(true);
            } else {
                pepnovoParameters.setEstimateCharge(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_CORRECT_PREC_MASS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_CORRECT_PREC_MASS.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                pepnovoParameters.setCorrectPrecursorMass(true);
            } else {
                pepnovoParameters.setCorrectPrecursorMass(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                pepnovoParameters.setDiscardLowQualitySpectra(true);
            } else {
                pepnovoParameters.setDiscardLowQualitySpectra(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                pepnovoParameters.setDiscardLowQualitySpectra(true);
            } else {
                pepnovoParameters.setDiscardLowQualitySpectra(false);
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_FRAGMENTATION_MODEL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_FRAGMENTATION_MODEL.id);
            pepnovoParameters.setFragmentationModel(arg);
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_GENERATE_BLAST.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_GENERATE_BLAST.id);
            Integer option = new Integer(arg);
            if (option == 1) {
                pepnovoParameters.setGenerateQuery(true);
            } else {
                pepnovoParameters.setGenerateQuery(false);
            }
        }
        searchParameters.setIdentificationAlgorithmParameter(Advocate.pepnovo.getIndex(), pepnovoParameters);

    }

    /**
     * Returns the search parameters.
     *
     * @return the search parameters
     */
    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    /**
     * Returns the file where to save the identification parameters.
     * 
     * @return the file where to save the identification parameters
     */
    public File getDestinationFile() {
        return destinationFile;
    }

    /**
     * Indicates whether the modifications should be printed on the screen.
     * 
     * @return true if the modifications should be printed on the screen
     */
    public Boolean isListMods() {
        return listMods;
    }

    /**
     * Verifies that modifications are correctly recognized.
     *
     * @param aLine the command line to validate
     * @return true if the startup was valid
     * @throws IOException
     */
    public static boolean isValidModifications(CommandLine aLine) throws IOException {
        boolean error = false;
        if (aLine.hasOption(IdentificationParametersCLIParams.FIXED_MODS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FIXED_MODS.id);
            try {
                ArrayList<String> args = CommandLineUtils.splitInput(arg);
                for (String ptmName : args) {
                    PTM ptm = PTMFactory.getInstance().getPTM(ptmName);
                    if (ptm == null || ptm == PTMFactory.unknownPTM) {
                        throw new IllegalArgumentException("PTM " + ptmName + " not found.");
                    }
                }
            } catch (Exception e) {
                if (!error) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the fixed modifications:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                }
                e.printStackTrace();
                error = true;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.VARIABLE_MODS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.VARIABLE_MODS.id);
            try {
                ArrayList<String> args = CommandLineUtils.splitInput(arg);
                for (String ptmName : args) {
                    PTM ptm = PTMFactory.getInstance().getPTM(ptmName);
                    if (ptm == null) {
                        throw new IllegalArgumentException("PTM " + ptmName + " not found.");
                    }
                }
            } catch (Exception e) {
                if (!error) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the variable modifications:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                }
                e.printStackTrace();
                error = true;
            }
        }
        return !error;
    }

    /**
     * Verifies the command line start parameters.
     *
     * @param aLine the command line to validate
     * @return true if the startup was valid
     * @throws IOException
     */
    public static boolean isValidStartup(CommandLine aLine) throws IOException {
        
        if (aLine.getOptions().length == 0) {
            return false;
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.MODS.id)) {
            return true;
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.PREC_PPM.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PREC_PPM.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the ppm/Da precursor ion parameter:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FRAG_PPM.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FRAG_PPM.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the ppm/Da precursor ion parameter:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PREC_TOL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PREC_TOL.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative value for the precursor tolerance.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the precursor mass tolerance parameter:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FRAG_TOL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FRAG_TOL.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative value for the precursor tolerance.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the precursor mass tolerance parameter:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.ENZYME.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.ENZYME.id);
            Enzyme option = EnzymeFactory.getInstance().getEnzyme(arg);
            if (option == null) {
                System.out.println(System.getProperty("line.separator") + "Enzyme " + arg + " not recognized."
                        + System.getProperty("line.separator"));
                return false;
            }
        }
        if (!aLine.hasOption(IdentificationParametersCLIParams.DB.id) || aLine.getOptionValue(IdentificationParametersCLIParams.DB.id).equals("")) {
            System.out.println(System.getProperty("line.separator") + "No database specified"
                    + System.getProperty("line.separator"));
            return false;
        } else {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.DB.id);
            File fastaFile = new File(arg);
            if (!fastaFile.exists()) {
                System.out.println(System.getProperty("line.separator") + "Database not found."
                        + System.getProperty("line.separator"));
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.MC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MC.id);
            try {
                int value = new Integer(arg);
                if (value < 0) {
                    throw new IllegalArgumentException("Found negative value for the number of missed cleavages.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the number of missed cleavages:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.MIN_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MIN_CHARGE.id);
            try {
                int value = new Integer(arg);
                if (value < 0) {
                    throw new IllegalArgumentException("Found negative value for the minimal charge.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the minimal charge:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.MAX_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.MAX_CHARGE.id);
            try {
                int value = new Integer(arg);
                if (value < 0) {
                    throw new IllegalArgumentException("Found negative value for the maximal charge.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the minimal charge:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_REMOVE_PREC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_REMOVE_PREC.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_REMOVE_PREC.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_SCALE_PREC.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_SCALE_PREC.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_SCALE_PREC.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_ESTIMATE_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_ESTIMATE_CHARGE.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_ESTIMATE_CHARGE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MAX_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MAX_EVALUE.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_MAX_EVALUE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_HITLIST_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_HITLIST_LENGTH.id);
            try {
                int value = new Integer(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_HITLIST_LENGTH.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MIN_PEP_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MIN_PEP_LENGTH.id);
            try {
                int value = new Integer(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_MIN_PEP_LENGTH.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_MAX_PEP_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_MAX_PEP_LENGTH.id);
            try {
                int value = new Integer(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_MAX_PEP_LENGTH.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.OMSSA_FORMAT.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.OMSSA_FORMAT.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.OMSSA_FORMAT.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_DYNAMIC_RANGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_DYNAMIC_RANGE.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_DYNAMIC_RANGE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_NPEAKS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_NPEAKS.id);
            try {
                int value = new Integer(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_NPEAKS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_FRAG_MZ.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_FRAG_MZ.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_MIN_FRAG_MZ.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_PEAKS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_PEAKS.id);
            try {
                int value = new Integer(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_MIN_PEAKS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_NOISE_SUPPRESSION.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_NOISE_SUPPRESSION.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_NOISE_SUPPRESSION.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_MIN_PREC_MASS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_MIN_PREC_MASS.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_MIN_PREC_MASS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_QUICK_ACETYL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_QUICK_ACETYL.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_QUICK_ACETYL.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_QUICK_PYRO.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_QUICK_PYRO.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_QUICK_PYRO.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_STP_BIAS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_STP_BIAS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_STP_BIAS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_EVALUE.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_EVALUE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_UNANTICIPATED_CLEAVAGE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SEMI.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SEMI.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_SEMI.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_POTENTIAL_MOD_FULL_REFINEMENT.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_POINT_MUTATIONS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_POINT_MUTATIONS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_POINT_MUTATIONS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SNAPS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SNAPS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_SNAPS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_REFINE_SPECTRUM_SYNTHESIS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_REFINE_SPECTRUM_SYNTHESIS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_REFINE_SPECTRUM_SYNTHESIS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_EVALUE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_EVALUE.id);
            try {
                double value = new Double(arg);
                if (value <= 0) {
                    throw new IllegalArgumentException("Negative or null value found.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_EVALUE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SEQUENCES.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SEQUENCES.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_OUTPUT_SEQUENCES.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.XTANDEM_OUTPUT_SPECTRA.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_OUTPUT_SPECTRA.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }

        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_HITLIST_LENGTH.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_HITLIST_LENGTH.id);
            try {
                int value = new Integer(arg);
                if (value <= 0 || value > 20) {
                    throw new IllegalArgumentException("Hitlist length should be between 1 and 20.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.XTANDEM_OUTPUT_PROTEINS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPTNOVO_ESTIMATE_CHARGE.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPTNOVO_ESTIMATE_CHARGE.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.PEPTNOVO_ESTIMATE_CHARGE.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_CORRECT_PREC_MASS.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_CORRECT_PREC_MASS.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.PEPNOVO_CORRECT_PREC_MASS.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.PEPNOVO_DISCARD_SPECTRA.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_FRAGMENTATION_MODEL.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_FRAGMENTATION_MODEL.id);
            if (!arg.equalsIgnoreCase("CID_IT_TRYP")) { // @TODO: support more models??
                System.out.println(System.getProperty("line.separator") + "Fragmentation model not supported." + System.getProperty("line.separator"));
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.PEPNOVO_GENERATE_BLAST.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.PEPNOVO_GENERATE_BLAST.id);
            try {
                int value = new Integer(arg);
                if (value != 0 && value != 1) {
                    throw new IllegalArgumentException("Found " + value + " where 0 or 1 was expected.");
                }
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while parsing the " + IdentificationParametersCLIParams.PEPNOVO_GENERATE_BLAST.id + " option."
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }
        if (aLine.hasOption(IdentificationParametersCLIParams.FRAG_PPM.id)) {
            String arg = aLine.getOptionValue(IdentificationParametersCLIParams.FRAG_PPM.id);
            try {
                new Integer(arg);
            } catch (Exception e) {
                System.out.println(System.getProperty("line.separator") + "An error occurred while reading the ppm/Da fragment ion parameter:"
                        + System.getProperty("line.separator") + e.getLocalizedMessage() + System.getProperty("line.separator"));
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}