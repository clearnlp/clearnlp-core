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
package com.clearnlp.reader;

import java.util.List;

import com.clearnlp.pos.POSNode;

/**
 * @since 1.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class POSReader extends AbstractColumnReader<POSNode[]>
{
	private int i_form;
	private int i_pos;
	
	/**
	 * Constructs a part-of-speech reader.
	 * @param iForm the column index of the word-form field.
	 * @param iPos the column index of the POS field.
	 */
	public POSReader(int iForm, int iPos)
	{
		init(iForm, iPos);
	}
	
	/**
	 * Initializes column indexes of fields.
	 * @param iForm the column index of the form field.
	 * @param iPos the column index of the POS field.
	 */
	public void init(int iForm, int iPos)
	{
		i_form = iForm;
		i_pos  = iPos;
	}
	
	@Override
	public POSNode[] next()
	{
		POSNode[] nodes = null;
		
		try
		{
			List<String[]> lines = readLines();
			if (lines == null)	return null;
			
			int i, size = lines.size();
			String  form;
			String[] tmp;
			
			nodes = new POSNode[size];
			
			for (i=0; i<size; i++)
			{
				tmp  = lines.get(i);
				form = tmp[i_form];
				
				if (i_pos < 0)	nodes[i] = new POSNode(form);
				else			nodes[i] = new POSNode(form, tmp[i_pos]);
			}
		}
		catch (Exception e) {e.printStackTrace();}
		
		return nodes;
	}
	
	public String getType()
	{
		return TYPE_POS;
	}
}
