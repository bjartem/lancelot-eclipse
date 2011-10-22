import re

import sys
from javax.swing import (BoxLayout, ImageIcon, JButton, JFrame, JPanel,
        JPasswordField, JLabel, JTextArea, JButton, JFileChooser, JScrollPane,
        SwingConstants, WindowConstants, UIManager,)
from java.awt import Component, GridLayout
from java.net import URL
from java.lang import Runnable

def _open():
    frame = JFrame('Galahad',
                   defaultCloseOperation=WindowConstants.EXIT_ON_CLOSE)
    panel = JPanel(GridLayout(5, 2))
    frame.add(panel)

    chosen_values = {}
    def create_file_choice_button(name, label_text):
        button = JButton('Click to select')
        label = JLabel(label_text)
        file_chooser = JFileChooser()
        
        def choose_file(event):
            user_did_choose_file = (file_chooser.showOpenDialog(frame) ==
                                   JFileChooser.APPROVE_OPTION)
            if user_did_choose_file:
                file_ = file_chooser.getSelectedFile();
                button.text = chosen_values[name] = str(file_)
        button.actionPerformed = choose_file

        panel.add(label)
        panel.add(button)

    create_file_choice_button('binary',     'Binary archive:')
    create_file_choice_button('source',     'Source archive:')
    create_file_choice_button('output_dir', 'Output directory:')

    panel.add(JLabel(''))
    panel.add(JLabel(''))

    def run_fn(event):
        log_window = JFrame('Galahad Log')
        log_text_area = JTextArea()
        log_text_area.editable = False
        log_window.setSize(400, 500)
        log_window.add(log_text_area)
        log_window.show()
        log_text_area.append('sdfsdfsdfsdfsd %d' % 3)
        
    panel.add(JButton('Run analysis', actionPerformed=run_fn))
    panel.add(JButton('Quit', actionPerformed=lambda e: sys.exit(0)))

    frame.setSize(300, 160)
    frame.visible = True

def open():
    try:
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    except:
        print 'Failed to enable native look and feel.'
    _open()
