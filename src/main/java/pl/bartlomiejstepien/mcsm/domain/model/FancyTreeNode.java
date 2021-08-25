package pl.bartlomiejstepien.mcsm.domain.model;

import java.util.ArrayList;
import java.util.List;

public class FancyTreeNode
{
    private String title;
    private String key;
    private boolean folder;
    private List<FancyTreeNode> children = new ArrayList<>();

    public FancyTreeNode(String title, boolean folder)
    {
        this.title = title;
        this.key = title;
        this.folder = folder;
    }

    public String getTitle()
    {
        return title;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public void addChild(FancyTreeNode node)
    {
        this.children.add(node);
    }

    public List<FancyTreeNode> getChildren()
    {
        return children;
    }

    @Override
    public String toString() {
        return "FancyTreeNode{" +
                "title='" + title + '\'' +
                ", key='" + key + '\'' +
                ", folder=" + folder +
                ", children=" + children +
                '}';
    }
}
