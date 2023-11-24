package no.hvl.past.graph.trees;

import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.expressions.EvaluatableExpression;
import no.hvl.past.names.Name;
import no.hvl.past.util.Multiplicity;
import no.hvl.past.util.Pair;
import org.slf4j.event.Level;


import java.util.*;
import java.util.function.Consumer;

public abstract class CursorConfig {


    public static class FilterConfig {
        private final FilterConfig parentFilter;
        private final ChannelConfiguration parentConfig;
        private Name filter;
        private boolean activation;
        private boolean silent = true;
        private final List<FilterConfig> children = new ArrayList<>();

        public FilterConfig waitForAnd(Name key) {
            FilterConfig child = new FilterConfig(this, parentConfig);
            this.children.add(child);
            child.activation = true;
            child.silent = true;
            child.filter = key;
            return child;
        }

        public FilterConfig allow(Name key) {
            FilterConfig child = new FilterConfig(this, parentConfig);
            this.children.add(child);
            child.activation = true;
            child.silent = false;
            child.filter = key;
            return child;
        }

        public FilterConfig ignore(Name key) {
            FilterConfig child = new FilterConfig(this, parentConfig);
            this.children.add(child);
            child.activation = false;
            child.silent = true;
            child.filter = key;
            return child;
        }


        public FilterConfig(FilterConfig parentFilter, ChannelConfiguration parentConfig) {
            this.parentFilter = parentFilter;
            this.parentConfig = parentConfig;
        }

        public TreeReceiver createFilterPipeline(TreeReceiver finalTarget) {
            if (!activation && filter == null) {
                return new TreeReceiver.Discard();
            }

            TreeReceiver result = finalTarget;
            if (!children.isEmpty()) {
                if (children.size() == 1) {
                    result = children.get(0).createFilterPipeline(finalTarget);
                } else {
                    List<TreeReceiver> childPiplines = new ArrayList<>();
                    for (FilterConfig childFilter : children) {
                        childPiplines.add(childFilter.createFilterPipeline(finalTarget));
                    }
                    result = new TreeReceiver.Multiplexer(childPiplines);
                }
            }

            if (filter == null) {
                return result;
            } else {
                if (activation) {
                    result = new TreeReceiver.ActivationFilter(filter, silent, false, result);
                } else {
                    result = new TreeReceiver.DeactivationFilter(filter, false, result);
                }
            }

            return result;
        }

        public FilterConfig endFilterBranch() {
            return parentFilter;
        }

        public ChannelConfiguration endFilter() {
            return parentConfig;
        }

    }


    public abstract static class ValidatorConfig {

        public abstract TreeValidator createValidator(Name tagName, TreeValidator parent);
    }


    public static class MultiplicityValidatorConfig extends ValidatorConfig {
        private final Multiplicity multiplicity;

        public MultiplicityValidatorConfig(Multiplicity multiplicity) {
            this.multiplicity = multiplicity;
        }


        @Override
        public TreeValidator createValidator(Name tagName, TreeValidator parent) {
            return new TreeValidator.MultiplicityValidator(parent, tagName, multiplicity);
        }
    }

    public static class ChannelConfiguration {

        static final String DEFAULT_ERROR_STREAM = "DEFAULT";

        private FilterConfig filterConfig;
        private final String channel;
        private final Map<Name, Name> renameMap = new HashMap<>();
        private final Set<Name> list = new HashSet<>();
        private boolean whiteOrBlackListAssinged = false;
        private boolean isBlacklist = true;
        private String errorStream = DEFAULT_ERROR_STREAM;

        private Level makeLogger = null;



        public FilterConfig ingressFilter() {
            FilterConfig filterConfig = new FilterConfig(null, this);
            filterConfig.activation = true;
            this.filterConfig = filterConfig;
            return filterConfig;
        }


        public ChannelConfiguration(String channel) {
            this.channel = channel;
        }

        public ChannelConfiguration configRename(Name from, Name to) {
            this.renameMap.put(from, to);
            return this;
        }

        public ChannelConfiguration enableLogging(Level logLevel) {
            this.makeLogger = logLevel;
            return this;
        }

        public ChannelConfiguration configWhitelist(Name feature) {
            if (whiteOrBlackListAssinged && isBlacklist) {
                throw new RuntimeException("You cannot assign a white-list after a black-list was already assigned");
            } else {
                whiteOrBlackListAssinged = true;
                isBlacklist = false;
            }
            this.list.add(feature);
            return this;
        }

        public String getChannel() {
            return channel;
        }

        public boolean supports(Name name) {
            if (isBlacklist) {
                return !list.contains(name);
            } else {
                return list.contains(name);
            }
        }



        public ChannelConfiguration configBlacklist(Name name) {
            if (whiteOrBlackListAssinged && !isBlacklist) {
                throw new RuntimeException("You cannot assign a black-list after a white-list was already assigned");
            } else {
                whiteOrBlackListAssinged = true;
                isBlacklist = true;
            }
            this.list.add(name);
            return this;
        }

        public ChannelConfiguration errorStream(String errorStreamName) {
            this.errorStream = errorStreamName;
            return this;
        }

        public String errorStream() {
            return errorStream;
        }

        public TreeReceiver.MapName getRenamer(TreeReceiver target) {
            TreeReceiver wrapped = target;
            if (makeLogger != null) {
                TreeReceiver.LoggingTreeReceiver logger = new TreeReceiver.LoggingTreeReceiver(makeLogger, TreeCollectCursor.Channel.class.getName() + ":" + channel);
                wrapped = new TreeReceiver.Multiplexer(Arrays.asList(logger, target));
            }
            return new TreeReceiver.MapName(wrapped) {
                @Override
                public Name map(Name in) {
                    if (renameMap.containsKey(in)) {
                        return renameMap.get(in);
                    }
                    return in;
                }
            };
        }

        public TreeReceiver getFilter(TreeReceiver target) {
            if (this.filterConfig != null) {
                TreeReceiver.ActivationFilter topLeveEvents = new TreeReceiver.ActivationFilter(ErrorValue.INSTANCE, true, true, target);
                return new TreeReceiver.Multiplexer(Arrays.asList(topLeveEvents, this.filterConfig.createFilterPipeline(target)));
            }
            return target;
        }
    }

    public interface CacheConfig {

        TreeCollectCursor.Cache provideCache();

    }


    public static class LocalCacheConfig implements CacheConfig {

        private final LinkedHashMap<String, EvaluatableExpression> keyExpressionMap = new LinkedHashMap<>();

        LocalCacheConfig() {
        }


        public LocalCacheConfig configureKeyExpression(String channel, EvaluatableExpression expression) {
            this.keyExpressionMap.put(channel, expression);
            return this;
        }


        @Override
        public TreeCollectCursor.Cache provideCache() {
            return new TreeCollectCursor.Cache(keyExpressionMap);
        }
    }




    public static class RootCursorConfig extends CursorConfig {

        private final Name rootElementName;
        private Name treeName;
        private TreeTypeLibrary typeLibrary;
        private Graph treeType;

        public RootCursorConfig setTreeName(Name treeName) {
            this.treeName = treeName;
            return this;
        }

        public RootCursorConfig setTypeLibrary(TreeTypeLibrary typeLibrary) {
            this.typeLibrary = typeLibrary;
            return this;
        }

        public RootCursorConfig setTreeType(Graph treeType) {
            this.treeType = treeType;
            return this;
        }

        RootCursorConfig(Name rootElementName) {
            this.rootElementName = rootElementName;
        }

        @Override
        public TreeCollectCursor create() {
            TreeCollectCursor.RootCursor result = new TreeCollectCursor.RootCursor(treeName != null);
            result.setRootName(rootElementName);
            if (treeName != null) {
                result.setTreeName(treeName);
                if (treeType != null) {
                    result.setTreeType(new Pair<>(treeType, typeLibrary != null ?
                            typeLibrary :
                            TreeTypeLibrary.fromGraphForRootElement(rootElementName.printRaw(), treeType)));
                }
            }
            for (ChannelConfiguration channel : super.channelConfigurations.values()) {
                result.registerChannel(channel);
            }
            for (Name feature : super.subFeatures.keySet()) {
                CursorConfig subConfig = super.subFeatures.get(feature);
                if (subConfig instanceof NestedCursorConfig) {
                    result.registerCollectedFiled(feature, ((NestedCursorConfig) subConfig).createSubCursor(result));
                } else {
                    result.registerCollectedFiled(feature, subConfig.create());
                }
            }
            if (validatorConfig != null) {
                result.registerValidator(getOrCreateValidator());
            }
            return result;
        }

        @Override
        public CursorConfig endCurrent() {
            return null;
        }

        @Override
        public TreeValidator getOrCreateValidator() {
            if (validatorConfig == null) {
                return null;
            } else {
                if (validator == null) {
                    validator = validatorConfig.createValidator(rootElementName, null);
                }
                return validator;
            }
        }
    }
    
    public static abstract class NestedCursorConfig extends CursorConfig {

        private final Name currentFeature;
        private final CursorConfig parentConfig;

        protected NestedCursorConfig(Name currentFeature, CursorConfig parentConfig) {
            this.channelConfigurations.putAll(parentConfig.channelConfigurations);
            this.currentFeature = currentFeature;
            this.parentConfig = parentConfig;
        }

        public Name getCurrentFeature() {
            return currentFeature;
        }

        @Override
        public CursorConfig endCurrent() {
            return parentConfig;
        }

        @Override
        public TreeCollectCursor create() {
            return createSubCursor(null);
        }


        public abstract TreeCollectCursor createSubCursor(TreeCollectCursor parent);

        @Override
        public TreeValidator getOrCreateValidator() {
            if (validatorConfig == null) {
                return null;
            } else {
                if (validator == null) {
                    validator = validatorConfig.createValidator(currentFeature, parentConfig.getOrCreateValidator());
                }
                return validator;
            }
        }
    }
    
    public static class ConstantCursorConfig extends NestedCursorConfig {

        private final List<TreeEvent> constantEvents = new ArrayList<>();
        private boolean isCollection = true;

        public ConstantCursorConfig add(TreeEvent event) {
            this.constantEvents.add(event);
            return this;
        }

        public ConstantCursorConfig addAll(Collection<TreeEvent> events) {
            this.constantEvents.addAll(events);
            return this;
        }

        public ConstantCursorConfig collectionValued() {
            this.isCollection = true;
            return this;
        }

        public ConstantCursorConfig singleValued() {
            this.isCollection = false;
            return this;
        }
        
        
        public ConstantCursorConfig(Name currentFeature, CursorConfig parentConfig) {
            super(currentFeature, parentConfig);
        }

        @Override
        public TreeCollectCursor createSubCursor(TreeCollectCursor parent) {
            TreeCollectCursor.ConstantCursor cursor = new TreeCollectCursor.ConstantCursor(parent, constantEvents);
            if (validatorConfig != null) {
                cursor.registerValidator(getOrCreateValidator());
            }
            return cursor;
        }


    }
    public static class ErrorRecorderCursorConfig extends NestedCursorConfig {
        private final String errorStream;
        private Name stackTraceFieldName;
        private int stackTraceDepth;
        private Name locationFieldName;
        private Name messageFieldName;


        public ErrorRecorderCursorConfig(Name currentFeature, CursorConfig parentConfig, String errorStream) {
            super(currentFeature, parentConfig);
            this.errorStream = errorStream;
            stackTraceDepth = Integer.MAX_VALUE;
        }


        public ErrorRecorderCursorConfig asObject() {
            messageFieldName = Name.identifier("errors");
            return this;
        }

        public ErrorRecorderCursorConfig messageName(Name messageFieldName) {
            this.messageFieldName = messageFieldName;
            return this;
        }

        public ErrorRecorderCursorConfig location(Name locationFieldName) {
            if (messageFieldName == null) {
                asObject();
            }
            this.locationFieldName = locationFieldName;
            return this;
        }

        public ErrorRecorderCursorConfig stackTrace(Name stackTraceFieldName, int stackTraceDepth) {
            if (messageFieldName == null) {
                asObject();
            }
            this.stackTraceFieldName = stackTraceFieldName;
            this.stackTraceDepth = stackTraceDepth;
            return this;
        }



        @Override
        public TreeCollectCursor createSubCursor(TreeCollectCursor parent) {
            return new TreeCollectCursor.ErrorCursor(parent, errorStream, messageFieldName, locationFieldName, stackTraceFieldName, stackTraceDepth);
        }
    }

    public static class LeafCursorConfig extends NestedCursorConfig {

        private final boolean isCollection;
        private boolean isCompacting;

        @Override
        public TreeCollectCursor createSubCursor(TreeCollectCursor parent) {
            TreeCollectCursor.LeafCollectCursor leafCollectCursor = new TreeCollectCursor.LeafCollectCursor(parent,
                    isCollection,
                    isCompacting);
            if (validatorConfig != null) {
                leafCollectCursor.registerValidator(getOrCreateValidator());
            }
            return leafCollectCursor;
        }


        public LeafCursorConfig(Name currentFeature,
                                CursorConfig parentConfig,
                                boolean isCollection,
                                boolean isCompacting) {
            super(currentFeature, parentConfig);
            this.isCollection = isCollection;
            this.isCompacting = isCompacting;
        }

        public LeafCursorConfig enableCompacting() {
            this.isCompacting = true;
            return this;
        }

        public LeafCursorConfig disableCompacting() {
            this.isCompacting = false;
            return this;
        }
    }

    public static class CollectionCursorConfig extends NestedCursorConfig {

        private CacheConfig cacheConfig;


        protected CollectionCursorConfig(Name currentFeature, CursorConfig parentConfig) {
            super(currentFeature, parentConfig);
        }



        @Override
        public TreeCollectCursor createSubCursor(TreeCollectCursor parent) {
            Set<String> waitingForChannels = new HashSet<>();
            LinkedHashMap<Name, NestedCursorConfig> subConfigs = new LinkedHashMap<>();
            for (Name feature : super.subFeatures.keySet()) {
                CursorConfig cursorConfig = subFeatures.get(feature);
                if (cursorConfig instanceof NestedCursorConfig) {
                    subConfigs.put(feature, (NestedCursorConfig) cursorConfig);
                }
                for (String channel : channelConfigurations.keySet()) {
                    if (!waitingForChannels.contains(channel) && channelConfigurations.get(channel).supports(getCurrentFeature())) {

                        if (channelConfigurations.get(channel).supports(feature)) {
                            waitingForChannels.add(channel);
                        }
                    }
                }
            }
            TreeCollectCursor result;
            if (cacheConfig != null) {
                result= new TreeCollectCursor.GlobalMergingCursor(parent, cacheConfig.provideCache(), subConfigs, waitingForChannels);
            } else {
                result= new TreeCollectCursor.ConcatenatingObjectsCollector(parent, subConfigs, waitingForChannels);
            }
            if (validatorConfig != null) {
                result.registerValidator(getOrCreateValidator());

            }

            return result;

        }

        public CollectionCursorConfig configureLocalCache(Consumer<LocalCacheConfig> cacheConfig) {
            this.cacheConfig = new LocalCacheConfig();
            cacheConfig.accept((LocalCacheConfig) this.cacheConfig);
            return this;
        }

    }

    public static class OneObjectCursorConfig extends NestedCursorConfig {

        protected OneObjectCursorConfig(Name currentFeature, CursorConfig parentConfig) {
            super(currentFeature, parentConfig);
        }

        @Override
        public TreeCollectCursor createSubCursor(TreeCollectCursor parent) {
            TreeCollectCursor.OneObjectCollector collector = new TreeCollectCursor.OneObjectCollector(parent);
            for (Name feature : super.subFeatures.keySet()) {
                CursorConfig cursorConfig = subFeatures.get(feature);
                if (cursorConfig instanceof NestedCursorConfig) {
                    NestedCursorConfig nestedConf = (NestedCursorConfig) cursorConfig;
                    TreeCollectCursor subCursor = nestedConf.createSubCursor(collector);
                    collector.addFeatureHandler(feature, subCursor);
                }
            }
            if (validatorConfig != null) {
                collector.registerValidator(getOrCreateValidator());
            }
            return collector;
        }
    }

    protected final LinkedHashMap<Name, CursorConfig> subFeatures = new LinkedHashMap<>();
    protected final Map<String, ChannelConfiguration> channelConfigurations = new HashMap<>();
    protected ValidatorConfig validatorConfig;
    protected TreeValidator validator;

    public abstract TreeCollectCursor create();

    public abstract CursorConfig endCurrent();


    public abstract TreeValidator getOrCreateValidator();



    // Factory methods

    public static RootCursorConfig rootConfig(Name rootElementName) {
        return new RootCursorConfig(rootElementName);
    }

    public CursorConfig validate(Multiplicity multiplicity) {
        this.validatorConfig = new MultiplicityValidatorConfig(multiplicity);
        return this;
    }

    public ConstantCursorConfig constant(Name fieldName) {
        ConstantCursorConfig config = new ConstantCursorConfig(fieldName, this);
        this.subFeatures.put(fieldName, config);
        return config;
    }

    public CursorConfig channel(String channelName, Consumer<ChannelConfiguration> channelConfigurer) {
        ChannelConfiguration configuration = new ChannelConfiguration(channelName);
        this.channelConfigurations.put(channelName, configuration);
        channelConfigurer.accept(configuration);
        return this;
    }

    public LeafCursorConfig leaf(Name fieldName, boolean isCollection) {
        LeafCursorConfig result = new LeafCursorConfig(fieldName, this, isCollection, false);
        this.subFeatures.put(fieldName, result);
        return result;
    }

    public OneObjectCursorConfig oneObject(Name fieldName) {
        OneObjectCursorConfig object = new OneObjectCursorConfig(fieldName, this);
        this.subFeatures.put(fieldName, object);
        return object;
    }

    public CollectionCursorConfig objects(Name fieldName) {
        CollectionCursorConfig objects = new CollectionCursorConfig(fieldName, this);
        this.subFeatures.put(fieldName, objects);
        return objects;
    }


    public ErrorRecorderCursorConfig erros(Name fieldName, String errorStream) {
        ErrorRecorderCursorConfig config = new ErrorRecorderCursorConfig(fieldName, this, errorStream);
        this.subFeatures.put(fieldName,config);
        return config;
    }

}
