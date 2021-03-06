//Copyright (c) 2006 - 2015, Markus Strauch.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.sdedit.ui.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.SequenceDiagram;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.text.AbstractTextHandler;
import net.sf.sdedit.text.TextHandler;

public class SequenceDiagramErrorHandler extends DiagramErrorHandler {
    
    private DiagramTextTab tab;

    public SequenceDiagramErrorHandler (DiagramTextTab tab) {
        this.tab = tab;
    }
    
    private SequenceDiagram diagram() {
        return (SequenceDiagram) tab.getDiagram();
    }
    
    protected void handleBug(Diagram diagram, RuntimeException ex) {

        String name = "sdedit-errorlog-" + System.currentTimeMillis();

        File errorLogFile = new File(name);
        try {
            errorLogFile.createNewFile();
        } catch (IOException e0) {
            try {
                errorLogFile = new File(System.getProperty("user.home"), name);
                errorLogFile.createNewFile();
            } catch (IOException e1) {
                errorLogFile = new File(System.getProperty("java.io.tmpdir",
                        name));
            }
        }

        try {
            saveLog(errorLogFile, ex, (TextHandler) diagram.getDataProvider());
        } catch (IOException e) {
           tab.get_UI().errorMessage(e, null,
                    "An error log file could not be saved.");
        }

    }

    @Override
    public void handleDiagramError(DiagramError error) {
        if (error == null) {
            tab.setError(false, "", -1, -1);
            if (diagram().getFragmentManager().openFragmentsExist()) {
                tab.setError(
                        true,
                        "Warning: There are open comments. Use [c:<type> <text>]...[/c]",
                        -1, -1);
            }

            int noteNumber = diagram().getNextFreeNoteNumber();
            if (noteNumber == 0) {
                tab.setStatus("");
            } else {
                tab.setStatus("Next note number: "
                        + diagram().getNextFreeNoteNumber());
            }
        } else {
            tab.setStatus("");
            if (error instanceof FatalError) {
                FatalError fatal = (FatalError) error;
                System.err
                        .println("********************************************************");
                System.err
                        .println("*                                                      *");
                System.err
                        .println("*            A FATAL ERROR HAS OCCURED.                *");
                System.err
                        .println("*                                                      *");
                System.err
                        .println("********************************************************");
                error.getCause().printStackTrace();
                // cautiously embedding this call into a try/catch-block
                try {
                    handleBug(diagram(), fatal.getCause());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                AbstractTextHandler handler = (TextHandler) error.getProvider();
                String prefix = "";

                if (error instanceof SemanticError) {
                    prefix = diagram().isThreaded()
                            && diagram().getCallerThread() != -1 ? "Thread "
                            + diagram().getCallerThread() + ": " : "";
                }
                tab.setError(false, prefix + error.getMessage(),
                        handler.getLineBegin() - 1, handler.getLineEnd());
            }
        }

    }
    
    private static final String getFatalErrorDescription(Throwable ex) {
        return "A FATAL ERROR has occurred: " + ex.getClass().getSimpleName();
    }
    
    private void saveLog(File logFile, Throwable exception,
            TextHandler textHandler) throws IOException {

        FileOutputStream stream = new FileOutputStream(logFile);
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                    stream, ConfigurationManager.getGlobalConfiguration()
                            .getFileEncoding()));
            BufferedReader bufferedReader = new BufferedReader(
                    new StringReader(textHandler.getText()));
            int error = textHandler.getLineNumber();
            printWriter.println(exception.getClass().getSimpleName()
                    + " has occurred in line " + error + "\n");
            int i = 0;
            for (;;) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    bufferedReader.close();
                    break;
                }
                line = line.trim();
                if (i == error - 1) {
                    line = ">>>>>>>>>>>>>> " + line;
                }
                printWriter.println(line);
                i++;
            }
            printWriter.println("\n\n:::::::::::::::::::::::::::::\n\n");
            exception.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
            tab.get_UI()
                    .errorMessage(
                            null,
                            "FATAL ERROR",
                            getFatalErrorDescription(exception)
                                    + "\n\nAn error log file has been saved under \n"
                                    + logFile.getAbsolutePath()
                                    + "\n\n"
                                    + "Please send an e-mail with this file as an attachment to:\n"
                                    + "sdedit@users.sourceforge.net");
        } finally {
            stream.close();
        }
    }

}
