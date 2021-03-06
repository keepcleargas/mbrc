package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.ProtocolHandler;

public class HandleHanshake implements ICommand {
    private ProtocolHandler handler;
    private MainDataModel model;

    @Inject public HandleHanshake(ProtocolHandler handler, MainDataModel model) {
        this.handler = handler;
        this.model = model;
    }

    public void execute(IEvent e) {
        if (!(Boolean) e.getData()) {
            handler.resetHandshake();
            model.setHandShakeDone(false);
        }
    }
}
