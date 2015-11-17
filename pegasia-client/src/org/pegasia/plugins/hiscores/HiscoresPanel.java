package org.pegasia.plugins.hiscores;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.pegasia.api.component.PegasiaPanel;
import org.pegasia.api.runescape.Skill;
import org.pegasia.api.runescape.SkillMap;
import org.pegasia.util.FileUtils;
import org.pegasia.util.ui.LimitedJTextField;

public class HiscoresPanel extends PegasiaPanel implements ActionListener, MouseListener {
	private static final DecimalFormat largeNumber = new DecimalFormat("###,###");
	private static ImageIcon icon16, icon32;
	private static HiscoresPanel instance = null;

	private JTextField field;
	private JLabel bottomLabel;

	private SkillMap stats = null;
	private ArrayList<HighscoreSkill> skills;
	private double combat;
	boolean hasSearched, found;
	
	public static void start() {
		if (instance == null)
			instance = new HiscoresPanel();
		instance.install();
	}
	
	public static void stop() {
		instance.uninstall();
	}

	public HiscoresPanel() {
		super("Highscore Lookup", "hiscores-panel", PegasiaPanel.PANEL_RIGHT);
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(4, 4, 4, 4) );

		stats = new SkillMap();
		combat = 0;
		hasSearched = false;
		found = false;

		field = new LimitedJTextField(12);
		field.addActionListener(this);
		this.add(field, BorderLayout.PAGE_START);

		// The order must be defined manually, as it is different from the highscore's order
		skills = new ArrayList<HighscoreSkill>();
		skills.add(new HighscoreSkill(Skill.ATTACK));
		skills.add(new HighscoreSkill(Skill.HITPOINTS));
		skills.add(new HighscoreSkill(Skill.MINING));

		skills.add(new HighscoreSkill(Skill.STRENGTH));
		skills.add(new HighscoreSkill(Skill.AGILITY));
		skills.add(new HighscoreSkill(Skill.SMITHING));

		skills.add(new HighscoreSkill(Skill.DEFENCE));
		skills.add(new HighscoreSkill(Skill.HERBLORE));
		skills.add(new HighscoreSkill(Skill.FISHING));

		skills.add(new HighscoreSkill(Skill.RANGED));
		skills.add(new HighscoreSkill(Skill.THIEVING));
		skills.add(new HighscoreSkill(Skill.COOKING));

		skills.add(new HighscoreSkill(Skill.PRAYER));
		skills.add(new HighscoreSkill(Skill.CRAFTING));
		skills.add(new HighscoreSkill(Skill.FIREMAKING));

		skills.add(new HighscoreSkill(Skill.MAGIC));
		skills.add(new HighscoreSkill(Skill.FLETCHING));
		skills.add(new HighscoreSkill(Skill.WOODCUTTING));

		skills.add(new HighscoreSkill(Skill.RUNECRAFT));
		skills.add(new HighscoreSkill(Skill.SLAYER));
		skills.add(new HighscoreSkill(Skill.FARMING));

		skills.add(new HighscoreSkill(Skill.CONSTRUCTION));
		skills.add(new HighscoreSkill(Skill.HUNTER));
		skills.add(new HighscoreSkill(Skill.OVERALL));

		JPanel skillsPanel = new JPanel(new GridLayout(0, 3));
		for (HighscoreSkill skill: skills) {
			skill.addMouseListener(this);
			skillsPanel.add(skill);
		}
		skillsPanel.setOpaque(false);
		this.add(skillsPanel, BorderLayout.CENTER);

		bottomLabel = new JLabel();
		bottomLabel.setFont(new Font(bottomLabel.getFont().getName(), Font.PLAIN, 16));
		setBottomText(null);
		this.add(bottomLabel, BorderLayout.PAGE_END);
	}

	private void setBottomText(Skill skill) {
		if (hasSearched == false) {
			bottomLabel.setText("<html><br /><br />");
		} else {
			if (!found)
				bottomLabel.setText("<html><br /><font color='red'>Player not found.");
			else if (skill == null)
				bottomLabel.setText("<html><br />Combat: " + combat);
			else
				bottomLabel.setText("<html>Rank: " + largeNumber.format(stats.getRank(skill)) +
						"<br />XP: " + largeNumber.format(stats.getXP(skill)) );
		}
	}
	
	@Override
	public ImageIcon getIcon16() {
		if (icon16 == null)
			icon16 = new ImageIcon(FileUtils.getBufferedImage("resources/plugin/hiscores/hiscores16.png"));
		return icon16;
	}
	
	@Override
	public ImageIcon getIcon32() {
		if (icon32 == null)
			icon32 = new ImageIcon(FileUtils.getBufferedImage("resources/plugin/hiscores/hiscores32.png"));
		return icon32;
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		if (e.getSource() == field) {
			final String name = field.getText();

			(new SwingWorker<SkillMap, Void>() {
				@Override
				protected SkillMap doInBackground() throws Exception {
					try {
						SkillMap stats = new SkillMap();
						stats.loadName(name);
						return stats;
					} catch (IOException e) {
						return null;
					}
				}

				@Override
				protected void done() {
					try {
						SkillMap otherStats = get();
						for (HighscoreSkill skill: skills)
							skill.loadPlayerStats(otherStats);
						if (otherStats != null)
							combat = ((int) (otherStats.getCombatLevel()*100)) / 100.0;
						else
							combat = 0;
							
						
						if (otherStats != null) {
							found = true;
							stats = otherStats;
						} else
							found = false;
						
						hasSearched = true;
						setBottomText(null);
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			}).execute();
		}
	}

	private static class HighscoreSkill extends JLabel {
		public final Skill skill;
		public int level = 1;

		public HighscoreSkill(Skill skill) {
			super();
			this.skill = skill;

			if (skill == Skill.OVERALL) {
				this.setHorizontalAlignment(JLabel.CENTER);
				this.setFont(new Font(getFont().getName(), Font.PLAIN, 20));
			} else {
				this.setIcon(skill.getIcon());
				this.setFont(new Font(getFont().getName(), Font.PLAIN, 16));
			}
			this.setVerticalTextPosition(JLabel.CENTER);
			blankText();
		}

		public void loadPlayerStats(SkillMap stats) {
			if (stats != null)  {
				level = stats.getLevel(skill);

				this.setText(Integer.toString(level));
			} else
				blankText();
		}

		private void blankText() {
			if (skill == Skill.OVERALL)
				this.setText("");
			else
				this.setText("--");
		}
	}

	@Override public void mouseEntered(MouseEvent e) {
		setBottomText(((HighscoreSkill)e.getSource()).skill);
	}

	@Override public void mouseExited(MouseEvent e) {
		setBottomText(null);
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}
