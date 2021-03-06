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
package com.clearnlp.component.state;

import com.clearnlp.classification.feature.FtrToken;
import com.clearnlp.classification.feature.JointFtrXml;
import com.clearnlp.dependency.DEPNode;
import com.clearnlp.dependency.DEPTree;

/**
 * @since 2.0.1
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class AbstractState
{
	protected DEPTree d_tree;
	protected int     t_size;
	
	public AbstractState(DEPTree tree)
	{
		setTree(tree);
	}
	
	abstract public Object   getGoldLabel();
	abstract public Object[] getGoldLabels();
	
//	====================================== TREE ======================================
	
	public DEPTree getTree()
	{
		return d_tree;
	}
	
	public int getTreeSize()
	{
		return t_size;
	}

	public void setTree(DEPTree tree)
	{
		d_tree = tree;
		t_size = tree.size();		
	}
	
//	====================================== NODE ======================================

	public DEPNode getNode(int id)
	{
		return d_tree.get(id);
	}
	
	protected DEPNode getNode(FtrToken token, int cIdx, int bIdx, int eIdx)
	{
		if (token.offset == 0)
			return d_tree.get(cIdx);

		cIdx += token.offset;
		
		if (bIdx < cIdx && cIdx < eIdx)
			return getNode(cIdx);
		
		return null;
	}
	
	protected DEPNode getNodeWithRelation(FtrToken token, DEPNode node)
	{
		if (node == null)	return null;
		
		if (token.relation != null)
		{
			     if (token.isRelation(JointFtrXml.R_H))		node = node.getHead();
			else if (token.isRelation(JointFtrXml.R_H2))	node = node.getGrandHead();
			else if (token.isRelation(JointFtrXml.R_LMD))	node = d_tree.getLeftMostDependent  (node.id);
			else if (token.isRelation(JointFtrXml.R_RMD))	node = d_tree.getRightMostDependent (node.id);
			else if (token.isRelation(JointFtrXml.R_LMD2))	node = d_tree.getLeftMostDependent  (node.id, 1);
			else if (token.isRelation(JointFtrXml.R_RMD2))	node = d_tree.getRightMostDependent (node.id, 1);
			else if (token.isRelation(JointFtrXml.R_LNS))	node = d_tree.getLeftNearestSibling (node.id);
			else if (token.isRelation(JointFtrXml.R_RNS))	node = d_tree.getRightNearestSibling(node.id);
		}
		
		return node;
	}
}
