package com.bttf.queosk.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
class TrieNode {
    Map<Character, TrieNode> children; // 문자와 자식 TrieNode를 매핑하는 맵
    boolean isEndOfWord; // 단어의 끝인지 여부를 나타내는 플래그

    TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
    }
}

@Getter
@AllArgsConstructor
@Component
public class AutoCompleteTrie {
    private TrieNode root;

    public AutoCompleteTrie() {
        this.root = new TrieNode();
    }

    // 단어를 Trie에 삽입하는 메서드
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            if (!current.getChildren().containsKey(c)) {
                // 현재 문자 노드에 해당 문자의 자식 노드가 없으면 생성
                current.getChildren().put(c, new TrieNode());
            }
            current = current.getChildren().get(c); // 다음 레벨의 노드로 이동
        }
        current.setEndOfWord(true); // 마지막 문자까지 삽입한 후에 단어의 끝 플래그를 설정
    }


    // 주어진 접두사(prefix)로 시작하는 단어를 검색하여 추천 리스트를 반환하는 메서드
    public List<String> autoComplete(String prefix) {
        TrieNode current = root;
        List<String> suggestions = new ArrayList<>();

        // prefix를 Trie에서 찾으면서 현재 노드를 업데이트
        for (char c : prefix.toCharArray()) {
            current = current.getChildren().get(c);
            if (current == null) {
                return suggestions; // 접두사(prefix)가 존재하지 않으면 빈 리스트 반환
            }
        }

        // 접두사(prefix)로 시작하는 모든 단어를 찾아 추천 리스트에 추가
        findWordsWithPrefix(current, prefix, suggestions);

        return suggestions;
    }

    // 접두사(prefix)로 시작하는 모든 단어를 검색하여 추천 리스트에 추가하는 재귀함수
    private void findWordsWithPrefix(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(prefix); // 현재 노드가 단어의 끝인 경우, 추천 리스트에 추가
        }
        node.getChildren().forEach((c, child) -> {
            findWordsWithPrefix(child, prefix + c, results); // 모든 자식 노드에 대해 재귀적으로 검색 수행
        });
    }

    // 입력된 단어만을 삭제하는 메서드
    public void delete(String word) {
        delete(root, word, 0);
    }

    private boolean delete(TrieNode node, String word, int index) {
        if (index == word.length()) {
            // 단어의 마지막 문자까지 도달했을 때
            if (!node.isEndOfWord) {
                // 해당 단어가 Trie에 존재하지 않으면 삭제할 필요가 없음
                return false;
            }
            node.isEndOfWord = false; // 단어의 끝을 표시하는 플래그를 false로 설정
            return node.children.isEmpty(); // 자식 노드가 없으면 해당 노드도 삭제해야 함
        }

        char c = word.charAt(index);
        TrieNode child = node.children.get(c);
        if (child == null) {
            // 단어의 문자가 Trie에 존재하지 않으면 삭제할 필요가 없음
            return false;
        }

        boolean shouldDeleteChild = delete(child, word, index + 1);

        if (shouldDeleteChild) {
            // 자식 노드가 삭제될 경우, 현재 노드의 children 맵에서도 제거
            node.children.remove(c);
            // 현재 노드에 다른 단어가 연결되어 있지 않으면 현재 노드도 삭제
            return node.children.isEmpty() && !node.isEndOfWord;
        }
        return false;
    }
}
