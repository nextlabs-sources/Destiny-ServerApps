/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 16, 2016
 *
 */
package com.nextlabs.destiny.console.dto.policymgmt.porting;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;

/**
 *
 * Represent the policy tree to policy porting[export/ import]
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Node root = new Node();

    public static void main(String... a) {
        PolicyDTO dto1 = new PolicyDTO();
        dto1.setId(110L);
        dto1.setFullName("ROOT_12/amila1");

        PolicyDTO dto2 = new PolicyDTO();
        dto2.setId(10L);
        dto2.setFullName("ROOT_12/amila1/policy2");

        PolicyDTO dto3 = new PolicyDTO();
        dto3.setId(12L);
        dto3.setFullName("ROOT_12/amila1/policy3");

        PolicyDTO dto4 = new PolicyDTO();
        dto4.setId(14L);
        dto4.setFullName("ROOT_12/amila3/policy12");

        PolicyDTO dto5 = new PolicyDTO();
        dto5.setId(16L);
        dto5.setFullName("ROOT_13/amila5/policy4/lol12/mupol12");

        PolicyDTO dto6 = new PolicyDTO();
        dto6.setId(17L);
        dto6.setFullName("ROOT_15/amila5/lol12/mupol12");

        PolicyDTO dto7 = new PolicyDTO();
        dto7.setId(18L);
        dto7.setFullName("ROOT_15/amila5/lol12/mupol12/mupol13");

        PolicyDTO dto8 = new PolicyDTO();
        dto8.setId(19L);
        dto8.setFullName("ROOT_15/amila5/lol12/mupol12/mupol14");

        PolicyTree tree = new PolicyTree();
        tree.addNode(dto1);
        tree.addNode(dto2);
        tree.addNode(dto3);
        tree.addNode(dto4);
        tree.addNode(dto5);
        Node node1 = tree.addNode(dto6);
        dto7.setId(29L);
        tree.addNode(dto7);
        dto7.setId(39L);
        tree.addNode(dto8);

        for (Node node : tree.getRoot().getChildren()) {

            System.out.println("::::::::: ROOT FOLDERS:" + node);

            getF(node);
        }

        System.out.println(":::::::::::::-->> " + tree.checkNodeExists(node1));

        System.out.println("SIZE :_--------->>" + tree.size());
    }

    public static void getF(Node node) {
        for (Node childNode : node.getChildren()) {
            System.out.println("::::::::: CHILD FOLDERS :"
                    + childNode.getData().getFullName());
            getF(childNode);
        }
    }

    public boolean checkNodeExists(Node searchNode) {
        return checkExists(this.root, searchNode);
    }

    public boolean checkExists(Node root, Node searchNode) {
        boolean exists = false;
        for (Node node : root.getChildren()) {
            if (!node.isFolder()
                    && node.getData() != null
                    && searchNode.getData().getId()
                            .equals(node.getData().getId())) {
                return true;
            }
            exists = checkExists(node, searchNode);
        }
        return exists;
    }

    public Node addNode(PolicyDTO policyDTO) {
        String fullName = policyDTO.getFullName();
        String[] splits = fullName.split("/", -1);
        policyDTO.getFullName();
        String policyFolder = splits[0];
        String policyName = "";
        String path = "";
        if (splits.length == 2) {
            policyName = splits[1];
        } else {
            int lastIndx = fullName.lastIndexOf('/');
            path = fullName.substring(0, lastIndx);
            policyName = fullName.substring((lastIndx + 1), fullName.length());
        }

        // search root has folder exists, else create folder
        Node folder = null;
        for (Node folderNode : root.getChildren()) {
            if (folderNode.getFolderName().equals(policyFolder)
                    && folderNode.isFolder()) {
                folder = folderNode;
                break;
            }
        }

        if (folder == null) {
            folder = new Node();
            folder.setFolder(true);
            folder.setFolderName(policyFolder);
            folder.setPath(policyFolder);
            root.getChildren().add(folder);
        }

        return insertNode(folder, path, policyName, policyDTO);
    }

    private Node insertNode(Node node, String path, String policyName,
            PolicyDTO policyDTO) {
        if (node.getPath().equals(path)) {
            Node newNode = new Node(policyDTO);
            newNode.setPath(policyDTO.getFullName());
            node.getChildren().add(newNode);
            return newNode;
        } else {
            for (Node current : node.getChildren()) {
                if (current.getPath().equals(path) || path.startsWith(current.getPath() + "/")) {
                    return insertNode(current, path, policyName, policyDTO);
                }
            }
        }

        // unable to find a matching path add to the root level
        policyDTO.setFullName(node.getPath() + "/" + policyName);
        Node newNode = new Node(policyDTO);
        newNode.setPath(node.getPath() + "/" + policyName);
        node.getChildren().add(newNode);
        return newNode;
    }

    public int size() {
        if (this.root == null) {
            return 0;
        }
        int count = 0;
        count = traverseAndCount(this.root);
        return count;
    }

    private int traverseAndCount(Node root) {
        int count = 0;
        for (Node node : root.getChildren()) {
            if (!node.isFolder()) {
                count++;
            }
            count += traverseAndCount(node);

        }
        return count;
    }

    public Node getRoot() {
        return root;
    }

}
