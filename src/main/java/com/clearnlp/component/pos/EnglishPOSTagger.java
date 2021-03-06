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
package com.clearnlp.component.pos;

import java.io.ObjectInputStream;
import java.util.Set;

import com.clearnlp.classification.feature.JointFtrXml;
import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.train.StringTrainSpace;
import com.clearnlp.component.morph.EnglishMPAnalyzer;
import com.clearnlp.component.state.POSState;
import com.clearnlp.constant.english.ENAux;
import com.clearnlp.constituent.CTLibEn;
import com.clearnlp.dependency.DEPNode;

/**
 * Part-of-speech tagger using document frequency cutoffs.
 * @since 1.3.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class EnglishPOSTagger extends AbstractPOSTagger
{
//	====================================== CONSTRUCTORS ======================================
	
	/** Constructs a part-of-speech tagger for collecting lexica. */
	public EnglishPOSTagger(JointFtrXml[] xmls, Set<String> sLsfs)
	{
		super(xmls, sLsfs);
	}
	
	/** Constructs a part-of-speech tagger for training. */
	public EnglishPOSTagger(JointFtrXml[] xmls, StringTrainSpace[] spaces, Object[] lexica)
	{
		super(xmls, spaces, lexica);
	}
	
	/** Constructs a part-of-speech tagger for developing. */
	public EnglishPOSTagger(JointFtrXml[] xmls, StringModel[] models, Object[] lexica)
	{
		super(xmls, models, lexica);
	}
	
	/** Constructs a part-of-speech tagger for bootsrapping. */
	public EnglishPOSTagger(JointFtrXml[] xmls, StringTrainSpace[] spaces, StringModel[] models, Object[] lexica)
	{
		super(xmls, spaces, models, lexica);
	}
	
	/** Constructs a part-of-speech tagger for decoding. */
	public EnglishPOSTagger(ObjectInputStream in)
	{
		super(in);
	}
	
//	====================================== ABSTRACT METHODS ======================================
	
	@Override
	protected void initMorphologicalAnalyzer()
	{
		mp_analyzer = new EnglishMPAnalyzer();
	}
	
	@Override
	protected boolean applyRules(POSState state)
	{
		DEPNode node = state.getInput();
		
		if (containsLowerSimplifiedForm(node)) return false;
		if (applyNNP(state)) return true;
		
		return false;
	}
	
	private boolean applyNNP(POSState state)
	{
		if (!isDecode()) return false;
		
		DEPNode node = state.getInput();
		DEPNode p2 = state.getNode(node.id-2);
		
		if (p2 != null)
		{
			DEPNode p1 = state.getNode(node.id-1);
			
			if (p2.lowerSimplifiedForm.endsWith("name") && p1.lowerSimplifiedForm.equals(ENAux.IS))
			{
				node.pos = CTLibEn.POS_NNP;
				return true;
			}
		}
		
		return false;
	}
}
