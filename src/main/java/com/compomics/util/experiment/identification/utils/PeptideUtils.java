package com.compomics.util.experiment.identification.utils;

import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidPattern;
import com.compomics.util.experiment.biology.aminoacids.sequence.AminoAcidSequence;
import com.compomics.util.experiment.biology.enzymes.Enzyme;
import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.biology.modifications.ModificationType;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideVariantMatches;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.compomics.util.parameters.identification.search.ModificationParameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class groups functions that can be used to work with peptides.
 *
 * @author Marc Vaudel
 */
public class PeptideUtils {

    /**
     * Returns a boolean indicating whether the peptide matches a decoy
     * sequence.
     *
     * @param peptide the peptide
     * @param sequenceProvider a sequence provider.
     *
     * @return a boolean indicating whether the peptide matches a decoy sequence
     */
    public static boolean isDecoy(Peptide peptide, SequenceProvider sequenceProvider) {

        return peptide.getProteinMapping().navigableKeySet().stream()
                .anyMatch(accession -> sequenceProvider.getDecoyAccessions().contains(accession));

    }

    /**
     * Returns the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping.
     *
     * @param peptide the peptide
     * @param accession the accession of the protein
     * @param index the position of the peptide on the protein sequence
     * @param nAa the number of amino acids to include
     * @param sequenceProvider the sequence provider
     *
     * @return the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping
     */
    public static String getAaBefore(Peptide peptide, String accession, int index, int nAa, SequenceProvider sequenceProvider) {

        return sequenceProvider.getSubsequence(accession, index - nAa - 1, index - 1);

    }

    /**
     * Returns the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping.
     *
     * @param peptide the peptide
     * @param nAa the number of amino acids to include
     * @param sequenceProvider the sequence provider
     *
     * @return the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping
     */
    public static TreeMap<String, String[]> getAaBefore(Peptide peptide, int nAa, SequenceProvider sequenceProvider) {

        return peptide.getProteinMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Arrays.stream(entry.getValue())
                                .mapToObj(index -> getAaAfter(peptide, entry.getKey(), index, nAa, sequenceProvider))
                                .toArray(String[]::new),
                        (a, b) -> {
                            throw new IllegalArgumentException("Duplicate key.");
                        },
                        TreeMap::new));

    }

    /**
     * Returns the amino acids before the given peptide as a string.
     *
     * @param peptide the peptide
     * @param accession the accession of the protein
     * @param index the position of the peptide on the protein sequence
     * @param nAa the number of amino acids to include
     * @param sequenceProvider the sequence provider
     *
     * @return the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping
     */
    public static String getAaAfter(Peptide peptide, String accession, int index, int nAa, SequenceProvider sequenceProvider) {

        return sequenceProvider.getSubsequence(accession, index + peptide.getSequence().length(), index + peptide.getSequence().length() + nAa);

    }

    /**
     * Returns the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping.
     *
     * @param peptide the peptide
     * @param nAa the number of amino acids to include
     * @param sequenceProvider the sequence provider
     *
     * @return the amino acids before the given peptide as a string in a map
     * based on the peptide protein mapping
     */
    public static TreeMap<String, String[]> getAaAfter(Peptide peptide, int nAa, SequenceProvider sequenceProvider) {

        return peptide.getProteinMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Arrays.stream(entry.getValue())
                                .mapToObj(index -> getAaAfter(peptide, entry.getKey(), index, nAa, sequenceProvider))
                                .toArray(String[]::new),
                        (a, b) -> {
                            throw new IllegalArgumentException("Duplicate key.");
                        },
                        TreeMap::new));

    }

    /**
     * Returns the peptide modifications as a string.
     *
     * @param peptide the peptide
     * @param variable if true, only variable Modifications are shown, false
     * return only the fixed Modifications
     *
     * @return the peptide modifications as a string
     */
    public static String getVariablePeptideModificationsAsString(Peptide peptide, boolean variable) {

        TreeMap<String, HashSet<Integer>> modMap = Arrays.stream(peptide.getVariableModifications())
                .collect(Collectors.groupingBy(ModificationMatch::getModification,
                        TreeMap::new,
                        Collectors.mapping(ModificationMatch::getSite,
                                Collectors.toCollection(HashSet::new))));

        return modMap.entrySet().stream()
                .map(entry -> getModificationString(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }

    /**
     * Returns the modification and sites as string in the form
     * modName(site1,site2).
     *
     * @param modificationName the name of the modification
     * @param sites the modification sites
     *
     * @return the modification and sites as string
     */
    private static String getModificationString(String modificationName, HashSet<Integer> sites) {

        String sitesString = sites.stream()
                .sorted()
                .map(site -> site.toString())
                .collect(Collectors.joining(","));

        StringBuilder sb = new StringBuilder(modificationName.length() + sitesString.length() + 2);

        sb.append(modificationName).append("(").append(sitesString).append(")");

        return sb.toString();
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with Modification tags, e.g,
     * &lt;mox&gt;. /!\ This method will work only if the Modification found in
     * the peptide are in the ModificationFactory. Modifications should be
     * provided indexed by site as follows: N-term modifications are at index 0,
     * C-term at sequence length + 1, and amino acid at 1-based index on the
     * sequence.
     *
     * @param modificationProfile the modification profile of the search
     * @param nTermAsString the annotated N-term
     * @param cTermAsString the annotated C-term
     * @param includeHtmlStartEndTags if true, start and end HTML tags are added
     * @param peptide the peptide to annotate
     * @param confidentModificationSites the confidently localized variable
     * modification sites indexed by site. 
     * @param representativeAmbiguousModificationSites the representative site
     * of the ambiguously localized variable modifications in a map: aa number
     * &gt; list of modifications (1 is the first AA) (can be null)
     * @param secondaryAmbiguousModificationSites the secondary sites of the
     * ambiguously localized variable modifications in a map: aa number &gt;
     * list of modifications (1 is the first AA) (can be null)
     * @param fixedModificationSites the fixed modification sites in a map: aa
     * number &gt; list of modifications (1 is the first AA) (can be null)
     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
     * Modification tags, e.g, &lt;mox&gt;, are used
     * @param useShortName if true the short names are used in the tags
     *
     * @return the tagged modified sequence as a string
     */
    public static String getTaggedModifiedSequence(Peptide peptide, String nTermAsString, String cTermAsString, ModificationParameters modificationProfile, String[] confidentModificationSites, String[] representativeAmbiguousModificationSites, String[] secondaryAmbiguousModificationSites, String[] fixedModificationSites, boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName) {

        String peptideSequence = peptide.getSequence();
        
        if (confidentModificationSites == null) {

            confidentModificationSites = new String[peptideSequence.length()];

        }

        if (representativeAmbiguousModificationSites == null) {

            representativeAmbiguousModificationSites = new String[peptideSequence.length()];

        }

        if (secondaryAmbiguousModificationSites == null) {

            secondaryAmbiguousModificationSites = new String[peptideSequence.length()];

        }

        if (fixedModificationSites == null) {

            fixedModificationSites = new String[peptideSequence.length()];

        }

        StringBuilder modifiedSequence = new StringBuilder(peptideSequence.length());

        if (useHtmlColorCoding && includeHtmlStartEndTags) {

            modifiedSequence.append("<html>");

        }

        modifiedSequence.append(nTermAsString).append('-');
        modifiedSequence.append(AminoAcidSequence.getTaggedModifiedSequence(modificationProfile, peptide.getSequence(), confidentModificationSites, representativeAmbiguousModificationSites, secondaryAmbiguousModificationSites, fixedModificationSites, useHtmlColorCoding, useShortName));
        modifiedSequence.append('-').append(cTermAsString);

        if (useHtmlColorCoding && includeHtmlStartEndTags) {

            modifiedSequence.append("</html>");

        }

        return modifiedSequence.toString();

    }

    /**
     * Returns the number of enzymatic termini for the given enzyme on this
     * protein at the given location.
     *
     * @param peptideStart the 0 based index of the peptide start on the protein
     * @param peptideEnd the 0 based index of the peptide end on the protein
     * @param proteinSequence the protein sequence
     * @param enzyme the enzyme to use
     *
     * @return true of the peptide is non-enzymatic
     */
    public static int getNEnzymaticTermini(int peptideStart, int peptideEnd, String proteinSequence, Enzyme enzyme) {

        int nEnzymatic = 0;

        if (peptideStart == 0) {

            nEnzymatic++;

        } else {

            char aaBefore = proteinSequence.charAt(peptideStart - 1);
            char aaAfter = proteinSequence.charAt(peptideStart);

            if (enzyme.isCleavageSite(aaBefore, aaAfter)) {

                nEnzymatic++;

            }

        }

        if (peptideEnd == proteinSequence.length() - 1) {

            nEnzymatic++;

        } else {

            char aaBefore = proteinSequence.charAt(peptideEnd);
            char aaAfter = proteinSequence.charAt(peptideEnd + 1);

            if (enzyme.isCleavageSite(aaBefore, aaAfter)) {

                nEnzymatic++;

            }
        }

        return nEnzymatic;
    }

    /**
     * Returns a boolean indicating whether the peptide is enzymatic using one
     * of the given enzymes.
     *
     * @param peptide the peptide
     * @param proteinAccession the accession of the protein
     * @param proteinSequence the sequence of the protein
     * @param enzymes the enzymes used for digestion
     *
     * @return a boolean indicating whether the peptide is enzymatic using one
     * of the given enzymes
     */
    public static boolean isEnzymatic(Peptide peptide, String proteinAccession, String proteinSequence, ArrayList<Enzyme> enzymes) {

        int[] startIndexes = peptide.getProteinMapping().get(proteinAccession);

        if (startIndexes == null) {

            return false;

        }

        return enzymes.stream()
                .anyMatch(enzyme -> Arrays.stream(startIndexes)
                .anyMatch(startIndex -> getNEnzymaticTermini(
                startIndex,
                peptide.getPeptideEnd(proteinAccession, startIndex),
                proteinSequence,
                enzyme) == 2));
    }

    /**
     * Returns a boolean indicating whether the peptide is enzymatic in at least
     * one protein using one of the given enzymes.
     *
     * @param peptide the peptide
     * @param sequenceProvider the sequence provider
     * @param enzymes the enzymes used for digestion
     *
     * @return a boolean indicating whether the peptide is enzymatic using one
     * of the given enzymes
     */
    public static boolean isEnzymatic(Peptide peptide, SequenceProvider sequenceProvider, ArrayList<Enzyme> enzymes) {

        return peptide.getProteinMapping().entrySet().stream()
                .anyMatch(entry -> isEnzymatic(
                peptide,
                entry.getKey(),
                sequenceProvider.getSequence(entry.getKey()),
                enzymes));

    }

    /**
     * Returns a boolean indicating whether the peptide needs variants to be
     * mapped to the given protein.
     *
     * @param peptide the peptide
     * @param accession the accession of the protein
     *
     * @return a boolean indicating whether the peptide needs variants to be
     * mapped to the given protein
     */
    public static boolean isVariant(Peptide peptide, String accession) {

        int[] indexesOnProtein = peptide.getProteinMapping().get(accession);
        HashMap<Integer, PeptideVariantMatches> variantOnProtein = peptide.getVariantMatches().get(accession);

        return indexesOnProtein.length == variantOnProtein.size();

    }

    /**
     * Returns an array of the possible modification sites for the given modification on the given peptide. N-term modifications are at index 0, C-term at sequence length + 1, and amino acid at 1-based index on the sequence.
     * 
     * @param peptide the peptide
     * @param modification the modification
     * @param sequenceProvider a protein sequence provider
     * @param modificationsSequenceMatchingParameters the sequence matching paramters to use for modifications
     * 
     * @return an array of the possible modification sites
     */
    public static int[] getPossibleModificationSites(Peptide peptide, Modification modification, SequenceProvider sequenceProvider, SequenceMatchingParameters modificationsSequenceMatchingParameters) {

        String peptideSequence = peptide.getSequence();
        ModificationType modificationType = modification.getModificationType();

        if (modificationType == ModificationType.modaa) {

            AminoAcidPattern aminoAcidPattern = modification.getPattern();

            if (aminoAcidPattern.length() == 1) {

                return aminoAcidPattern.getIndexes(peptideSequence, modificationsSequenceMatchingParameters);

            } else if (aminoAcidPattern.length() > 1) {

                int minIndex = aminoAcidPattern.getMinIndex();
                int maxIndex = aminoAcidPattern.getMaxIndex();
                IntStream allPossibleIndexes = IntStream.empty();

                for (Entry<String, int[]> entry : peptide.getProteinMapping().entrySet()) {

                    String accession = entry.getKey();
                    String sequence = sequenceProvider.getSequence(accession);

                    for (int startIndex : entry.getValue()) {

                        StringBuilder extendedSequenceBuilder = new StringBuilder(peptideSequence.length() + aminoAcidPattern.length());

                        if (minIndex < 0) {

                            String prefix = sequence.substring(startIndex + minIndex, startIndex);
                            extendedSequenceBuilder.append(prefix);

                        }

                        extendedSequenceBuilder.append(peptideSequence);

                        if (maxIndex > 0) {

                            String suffix = sequence.substring(startIndex + peptideSequence.length(), startIndex + peptideSequence.length() + maxIndex);
                            extendedSequenceBuilder.append(suffix);

                        }

                        int[] sitesAtIndex = aminoAcidPattern.getIndexes(extendedSequenceBuilder.toString(), modificationsSequenceMatchingParameters);
                        allPossibleIndexes = IntStream.concat(allPossibleIndexes, Arrays.stream(sitesAtIndex));

                    }
                }

                allPossibleIndexes = allPossibleIndexes
                        .distinct()
                        .sorted();

                if (minIndex < 0) {

                    allPossibleIndexes.map(site -> site - minIndex);

                }

                return allPossibleIndexes.toArray();

            } else {

                throw new IllegalArgumentException("No pattern set for modification " + modification.getName() + ".");

            }

        } else if (modificationType == ModificationType.modnaa_peptide) {

            AminoAcidPattern aminoAcidPattern = modification.getPattern();

            if (aminoAcidPattern.length() == 1) {

                return aminoAcidPattern.matches(Character.toString(peptideSequence.charAt(0)), modificationsSequenceMatchingParameters) ? new int[]{0} : new int[0];

            } else if (aminoAcidPattern.length() > 1) {

                int minIndex = aminoAcidPattern.getMinIndex();
                int maxIndex = aminoAcidPattern.getMaxIndex();

                if (minIndex == 0 && maxIndex < peptideSequence.length()) {

                    return aminoAcidPattern.matches(peptideSequence.substring(0, maxIndex + 1), modificationsSequenceMatchingParameters) ? new int[]{0} : new int[0];

                }

                for (Entry<String, int[]> entry : peptide.getProteinMapping().entrySet()) {

                    String accession = entry.getKey();
                    String sequence = sequenceProvider.getSequence(accession);

                    for (int startIndex : entry.getValue()) {

                        int tempStart = startIndex + minIndex;
                        int tempEnd = startIndex + maxIndex + 1;

                        if (tempStart >= 0 && tempEnd <= sequence.length()) {

                            String subSequence = sequence.substring(tempStart, tempEnd);

                            if (aminoAcidPattern.matches(subSequence, modificationsSequenceMatchingParameters)) {
                                return new int[]{0};
                            }
                        }
                    }
                }

                return new int[0];

            } else {

                throw new IllegalArgumentException("No pattern set for modification " + modification.getName() + ".");

            }

        } else if (modificationType == ModificationType.modn_protein) {

            return peptide.getProteinMapping().values().stream()
                    .flatMapToInt(indexes -> Arrays.stream(indexes))
                    .anyMatch(index -> index == 0) ? new int[]{0} : new int[0];

        } else if (modificationType == ModificationType.modn_peptide) {

            return new int[]{0};

        } else if (modificationType == ModificationType.modnaa_protein) {

            String[] accessions = peptide.getProteinMapping().entrySet().stream()
                    .filter(entry -> Arrays.stream(entry.getValue()).anyMatch(index -> index == 0))
                    .map(entry -> entry.getKey())
                    .toArray(String[]::new);

            if (accessions.length > 0) {

                AminoAcidPattern aminoAcidPattern = modification.getPattern();

                if (aminoAcidPattern.length() == 1) {

                    return aminoAcidPattern.matches(Character.toString(peptideSequence.charAt(0)), modificationsSequenceMatchingParameters) ? new int[]{0} : new int[0];

                } else if (aminoAcidPattern.length() > 1) {

                    int minIndex = aminoAcidPattern.getMinIndex();

                    if (minIndex < 0) {
                        return new int[0];
                    }

                    int maxIndex = aminoAcidPattern.getMaxIndex();

                    if (maxIndex < peptideSequence.length()) {

                        return aminoAcidPattern.matches(peptideSequence.substring(0, maxIndex + 1), modificationsSequenceMatchingParameters) ? new int[]{0} : new int[0];

                    }

                    for (String accession : accessions) {

                        String sequence = sequenceProvider.getSequence(accession);

                        if (maxIndex < sequence.length()) {

                            String subSequence = sequence.substring(0, maxIndex + 1);

                            if (aminoAcidPattern.matches(subSequence, modificationsSequenceMatchingParameters)) {

                                return new int[]{0};

                            }
                        }
                    }
                } else {

                    throw new IllegalArgumentException("No pattern set for modification " + modification.getName() + ".");

                }
            }

            return new int[0];

        } else if (modificationType == ModificationType.modc_peptide) {

            return new int[]{peptideSequence.length() + 1};

        } else if (modificationType == ModificationType.modc_protein) {

            return peptide.getProteinMapping().entrySet().stream()
                    .anyMatch(entry -> sequenceProvider.getSequence(entry.getKey()).length() == entry.getValue()[entry.getValue().length - 1] + peptideSequence.length()) ? new int[]{peptideSequence.length() + 1} : new int[0];

        } else if (modificationType == ModificationType.modcaa_peptide) {

            AminoAcidPattern aminoAcidPattern = modification.getPattern();

            if (aminoAcidPattern.length() == 1) {

                return aminoAcidPattern.matches(Character.toString(peptideSequence.charAt(peptideSequence.length() - 1)), modificationsSequenceMatchingParameters) ? new int[]{peptideSequence.length() + 1} : new int[0];

            } else if (aminoAcidPattern.length() > 1) {

                int minIndex = aminoAcidPattern.getMinIndex();
                int maxIndex = aminoAcidPattern.getMaxIndex();
                int tempStart = peptideSequence.length() + minIndex;

                if (maxIndex == 0 && tempStart > 0) {

                    return aminoAcidPattern.matches(peptideSequence.substring(tempStart, peptideSequence.length()), modificationsSequenceMatchingParameters) ? new int[]{peptideSequence.length() + 1} : new int[0];

                }

                for (Entry<String, int[]> entry : peptide.getProteinMapping().entrySet()) {

                    String accession = entry.getKey();
                    String sequence = sequenceProvider.getSequence(accession);

                    for (int startIndex : entry.getValue()) {

                        int tempStartProtein = startIndex + tempStart;
                        int tempEndProtien = startIndex + peptideSequence.length() + maxIndex + 1;

                        if (tempStartProtein >= 0 && tempEndProtien <= sequence.length()) {

                            String subSequence = sequence.substring(tempStartProtein, tempEndProtien);

                            if (aminoAcidPattern.matches(subSequence, modificationsSequenceMatchingParameters)) {

                                return new int[]{peptideSequence.length() + 1};

                            }
                        }
                    }
                }

                return new int[0];

            } else {

                throw new IllegalArgumentException("No pattern set for modification " + modification.getName() + ".");

            }

        } else if (modificationType == ModificationType.modcaa_protein) {

            String[] accessions = peptide.getProteinMapping().entrySet().stream()
                    .filter(entry -> Arrays.stream(entry.getValue()).anyMatch(index -> index + peptideSequence.length() == sequenceProvider.getSequence(entry.getKey()).length()))
                    .map(entry -> entry.getKey())
                    .toArray(String[]::new);

            if (accessions.length > 0) {

                AminoAcidPattern aminoAcidPattern = modification.getPattern();

                if (aminoAcidPattern.length() == 1) {

                    return aminoAcidPattern.matches(Character.toString(peptideSequence.charAt(peptideSequence.length() - 1)), modificationsSequenceMatchingParameters) ? new int[]{peptideSequence.length() + 1} : new int[0];

                } else if (aminoAcidPattern.length() > 1) {

                    int maxIndex = aminoAcidPattern.getMaxIndex();

                    if (maxIndex > 0) {
                        return new int[0];
                    }

                    int minIndex = aminoAcidPattern.getMinIndex();
                    int tempStart = peptideSequence.length() + minIndex;

                    if (tempStart > 0) {

                        return aminoAcidPattern.matches(peptideSequence.substring(tempStart, peptideSequence.length()), modificationsSequenceMatchingParameters) ? new int[]{peptideSequence.length() + 1} : new int[0];

                    }

                    for (String accession : accessions) {

                        String sequence = sequenceProvider.getSequence(accession);

                        int tempStartProtein = sequence.length() - aminoAcidPattern.length() - 1;

                        if (tempStartProtein >= 0) {

                            String subSequence = sequence.substring(tempStartProtein, sequence.length());

                            if (aminoAcidPattern.matches(subSequence, modificationsSequenceMatchingParameters)) {

                                return new int[]{sequence.length()};

                            }
                        }
                    }

                } else {

                    throw new IllegalArgumentException("No pattern set for modification " + modification.getName() + ".");

                }
            }

            return new int[0];

        } else {

            throw new UnsupportedOperationException("Modification mapping not supported for modification of type " + modificationType + ".");

        }
    }
}