package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.Rectangle;

import javax.swing.*;

import WindowSerializer.Downloader;
import WindowSerializer.Saver;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private String name = "logger";

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        updateLogContent();
        new Downloader(this, name).download();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void unregisterListener()
    {
        m_logSource.unregisterListener(this);
    }

    @Override
    public void dispose()
    {
        Rectangle bounds = this.getBounds();
        Saver size = new Saver(bounds.x,bounds.y, bounds.width, bounds.height, this.isIcon);
        size.save(name);
        m_logSource.unregisterListener(this);
        super.dispose();
    }
}
