/*******************************************************************************
 * Copyright 2011 The fangorn project
 * 
 *        Author: Sumukh Ghodke
 * 
 *        Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 * 
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 ******************************************************************************/
package au.edu.unimelb.csse.search.join;

public interface EarlyStopWithFCJoin {
	TermJoinAware DESCENDANT = new EarlyStopFTLWithFirstCheckJoin(
			JoinLogic.DESCENDANT);

	TermJoinAware ANCESTOR = new EarlyStopLTFWithFirstCheckJoin(
			JoinLogic.ANCESTOR);

	TermJoinAware CHILD = new EarlyStopFTLWithFirstCheckJoin(JoinLogic.CHILD);

	TermJoinAware PARENT = new EarlyStopLTFWithFirstCheckJoin(JoinLogic.PARENT);

	TermJoinAware FOLLOWING_SIBLING = new EarlyStopFTLWithFirstCheckJoin(
			JoinLogic.FOLLOWING_SIBLING);

	TermJoinAware PRECEDING_SIBLING = new EarlyStopLTFWithFirstCheckJoin(
			JoinLogic.PRECEDING_SIBLING);

	TermJoinAware IMMEDIATE_FOLLOWING_SIBLING = new EarlyStopFTLWithFirstCheckJoin(
			JoinLogic.IMMEDIATE_FOLLOWING_SIBLING);

	TermJoinAware IMMEDIATE_PRECEDING_SIBLING = new EarlyStopLTFWithFirstCheckJoin(
			JoinLogic.IMMEDIATE_PRECEDING_SIBLING);

	TermJoinAware FOLLOWING = new EarlyStopFTLWithFirstCheckJoin(
			JoinLogic.FOLLOWING);

	TermJoinAware PRECEDING = new EarlyStopLTFWithFirstCheckJoin(
			JoinLogic.PRECEDING);

	TermJoinAware IMMEDIATE_FOLLOWING = new EarlyStopFTLWithFirstCheckJoin(
			JoinLogic.IMMEDIATE_FOLLOWING);

	TermJoinAware IMMEDIATE_PRECEDING = new EarlyStopLTFWithFirstCheckJoin(
			JoinLogic.IMMEDIATE_PRECEDING);
}
