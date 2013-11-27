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
package com.clearnlp.classification.algorithm.online;

import java.util.Collections;
import java.util.List;

import com.clearnlp.classification.instance.IntInstance;
import com.clearnlp.classification.model.StringOnlineModel;
import com.clearnlp.classification.prediction.IntPrediction;
import com.clearnlp.classification.vector.SparseFeatureVector;
import com.clearnlp.util.UTMath;

/**
 * AdaGrad algorithm using hinge loss.
 * @since 1.3.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class OnlineAdaGradHinge extends AbstractOnlineAdaGrad
{
	/**
	 * @param alpha the learning rate.
	 * @param rho the smoothing denominator.
	 */
	public OnlineAdaGradHinge(double alpha, double rho)
	{
		super(alpha, rho);
	}
	
	@Override
	protected boolean update(StringOnlineModel model, IntInstance instance, double[] gs)
	{
		IntPrediction max = getPrediction(model, instance, gs);
		
		if (max.label != instance.getLabel())
		{
			updateCounts (model, instance, gs, instance.getLabel(), max.label);
			updateWeights(model, instance, gs, instance.getLabel(), max.label);
			return true;
		}
		
		return false;
	}
	
	private IntPrediction getPrediction(StringOnlineModel model, IntInstance instance, double[] gs)
	{
		List<IntPrediction> ps = model.getIntPredictions(instance.getFeatureVector());
	
		ps.get(instance.getLabel()).score -= 1d;
		return Collections.max(ps);
	}
	
	private void updateCounts(StringOnlineModel model, IntInstance instance, double[] gs, int yp, int yn)
	{
		SparseFeatureVector x = instance.getFeatureVector();
		int i, len = x.size();
		double d;
		
		for (i=0; i<len; i++)
		{
			d = UTMath.sq(x.getWeight(i));
			
			gs[model.getWeightIndex(yp, x.getIndex(i))] += d;
			gs[model.getWeightIndex(yn, x.getIndex(i))] += d;
		}
	}
	
	private void updateWeights(StringOnlineModel model, IntInstance instance, double[] gs, int yp, int yn)
	{
		SparseFeatureVector x = instance.getFeatureVector();
		int i, xi, len = x.size();
		double vi, cost;
		
		for (i=0; i<len; i++)
		{
			xi = x.getIndex(i);
			vi = x.getWeight(i);
			
			cost = getCost(model, gs, yp, xi) * vi;
			model.updateWeight(yp, xi, (float)cost);
			
			cost = -getCost(model, gs, yn, xi) * vi;
			model.updateWeight(yn, xi, (float)cost);
		}
	}
}	