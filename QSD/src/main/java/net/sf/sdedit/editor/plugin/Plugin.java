// Copyright (c) 2006 - 2015, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.editor.plugin;

import javax.swing.Action;
import javax.swing.ImageIcon;

import net.sf.sdedit.config.Configuration;

public interface Plugin {

	// public void install ();

	public static final String DEFAULT_CONF_TITLE = "DEFAULT_CONF_TITLE";

	public static final String DEFAULT_CONF_RESTORE = "DEFAULT_CONF_RESTORE";

	public static final String DEFAULT_CONF_SAVE = "DEFAULT_CONF_SAVE";

	public static final String DEFAULT_CONF_DESCRIPTION = "DEFAULT_CONF_DESCRIPTION";

	public static final String LOCAL_CONF_TITLE = "LOCAL_CONF_TITLE";

	public static final String LOCAL_CONF_RESTORE = "LOCAL_CONF_RESTORE";

	public static final String LOCAL_CONF_SAVE = "LOCAL_CONF_SAVE";

	public static final String LOCAL_CONF_DESCRIPTION = "LOCAL_CONF_DESCRIPTION";

	public Class<? extends Configuration> getConfigurationClass();

	public Action getNewTabAction();

	public Iterable<FileHandler> getFileHandlers();

	public String getText(String textId);
	
	public String getCategory();
	
	public ImageIcon getIcon();

}
