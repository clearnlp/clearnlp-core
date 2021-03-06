/**
 * Copyright (c) 2009/09-2012/08, Regents of the University of Colorado
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Copyright 2012/09-2013/04, 2013/11-Present, University of Massachusetts Amherst
 * Copyright 2013/05-2013/10, IPSoft Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.clearnlp.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.segmentation.AbstractSegmenter;
import com.clearnlp.util.UTArray;
import com.clearnlp.util.UTInput;
import com.clearnlp.util.UTOutput;

/**
 * @since 1.1.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class DemoMultiThread
{
	final String s_language = AbstractReader.LANG_EN;
//	AbstractComponent[] c_components;
	
	public DemoMultiThread(String modelType, String inputFile, String outputDir, int numThreads) throws Exception
	{
//		AbstractComponent tagger = NLPGetter.getComponent(modelType, s_language, NLPMode.MODE_POS);
//		AbstractComponent parser = NLPGetter.getComponent(modelType, s_language, NLPMode.MODE_DEP);
//		c_components = new AbstractComponent[]{tagger, parser};
		
		process(inputFile, outputDir, numThreads);
	}
	
	public void process(String inputFile, String outputDir, int numThreads) throws Exception
	{
		BufferedReader reader = UTInput.createBufferedFileReader(inputFile);
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		PrintStream[] fout = getPrintStreams(outputDir, numThreads);
		Callable<String> thread;
		Future<String> future;
		String line;
		long pc;
		int i;
		
		for (i=0,pc=1; (line = reader.readLine().trim()) != null; i++,i%=numThreads,pc++)
		{
			if (line.isEmpty()) continue;
			thread = new DecodeTask(line);
			future = executor.submit(thread);
			fout[i].print(future.get());
			if (pc%1000 == 0) System.out.println(pc);
		}
		
		for (PrintStream f : fout) f.close();
		executor.shutdown();
		reader.close();
	}
	
	PrintStream[] getPrintStreams(String outputDir, int numThreads) throws Exception
	{
		PrintStream[] fout = new PrintStream[numThreads];
		int i;
		
		for (i=0; i<numThreads; i++)
			fout[i] = UTOutput.createPrintBufferedGZipFileStream(outputDir + "/" + i + ".gz");
		
		return fout;
	}
	
	class DecodeTask implements Callable<String>
	{
		String s_line;
		
		public DecodeTask(String line)
		{
			s_line  = line;
		}
		
		public String call()
		{
			AbstractSegmenter segmenter = NLPGetter.getSegmenter(s_language, NLPGetter.getTokenizer(s_language));
			BufferedReader reader = new BufferedReader(new StringReader(s_line));
			StringBuilder build = new StringBuilder();
			
			for (List<String> tokens : segmenter.getSentences(reader))
			{
				if (tokens.size() < 3)	continue;
				build.append(UTArray.join(tokens, "\n"));
				build.append("\n\n");
			}
			
			try
			{
				reader.close();
			}
			catch (IOException e) {e.printStackTrace();}
			
			return build.toString();
		}
    }

	public static void main(String[] args)
	{
		String inputFile  = args[0];
		String outputDir  = args[1];
		int    numThreads = Integer.parseInt(args[2]);
		
		try
		{
			new DemoMultiThread("general-en", inputFile, outputDir, numThreads);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
