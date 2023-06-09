package com.github.scalvetr;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.util.Collector;
import org.apache.flink.util.Preconditions;
import com.github.scalvetr.util.WordCountData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the "WordCount" program that computes a simple word occurrence histogram over text
 * files.
 *
 * <p>The input is a plain text file with lines separated by newline characters.
 *
 * <p>Usage: <code>WordCount --input &lt;path&gt; --output &lt;path&gt;</code><br>
 * If no parameters are provided, the program is run with default data from {@link WordCountData}.
 *
 * <p>This example shows how to:
 *
 * <ul>
 *   <li>write a simple Flink program.
 *   <li>use Tuple data types.
 *   <li>write and use user-defined functions.
 * </ul>
 */
public class WordCountJob {
    private static final Logger LOG = LoggerFactory.getLogger(WordCountJob.class);

    // *************************************************************************
    //     PROGRAM
    // *************************************************************************

    public static void main(String[] args) throws Exception {
        LOG.info("WordCountJob");

        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);

        // set up the execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        LOG.info("config -> input={}", params.get("input"));
        LOG.info("config -> output={}", params.get("output"));

        // make parameters available testdata the web interface
        env.getConfig().setGlobalJobParameters(params);

        // get input data
        DataSet<String> text = null;
        if (params.has("input")) {
            // union all the inputs from text files
            for (String input : params.getMultiParameterRequired("input")) {
                LOG.info("reading input -> {}", input);
                if (text == null) {
                    text = env.readTextFile(input);
                } else {
                    text = text.union(env.readTextFile(input));
                }
            }
            Preconditions.checkNotNull(text, "Input DataSet should not be null.");
        } else {
            // get default test text data
            LOG.info("Executing WordCount example with default input data set.");
            LOG.info("Use --input to specify file input.");
            text = WordCountData.getDefaultTextLineDataSet(env);
        }

        DataSet<Tuple2<String, Integer>> counts =
                // split up the lines testdata pairs (2-tuples) containing: (word,1)
                text.flatMap(new Tokenizer())
                        // group by the tuple field "0" and sum up tuple field "1"
                        .groupBy(0)
                        .sum(1);

        // emit result
        if (params.has("output")) {
            LOG.info("writing to output -> {}", params.get("output"));
            counts.writeAsCsv(params.get("output"), "\n", " ");
            // execute program
            env.execute("WordCount Example");
        } else {
            LOG.info("Printing result to stdout.");
            LOG.info("Use --output to specify output path.");
            counts.print();
        }
    }

    // *************************************************************************
    //     USER FUNCTIONS
    // *************************************************************************

    /**
     * Implements the string tokenizer that splits sentences into words as a user-defined
     * FlatMapFunction. The function takes a line (String) and splits it into multiple pairs testdata the
     * form of "(word,1)" ({@code Tuple2<String, Integer>}).
     */
    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}