package com.sun.java8.forkjoin;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhaochen
 * @date 2018/8/17
 * @desc 统计文件中出现超过指定次数的单词
 * args[0]:文件目录
 * args[1]:需要统计的单词
 * args[2]:指定次数
 */
public class CountWordsOfFiles{

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    public static void main(String[] args) throws IOException{
        CountWordsOfFiles cwff = new CountWordsOfFiles();
        Folder folder = Folder.fromDirectory(new File(args[0]));
        //decode可以分析数字，比如：010->8 10->10 #10|0x10 -> 16
        final int repeatCount = Integer.decode(args[2]);
        long counts;
        long startTime;
        long stopTime;

        long[] singleThreadTimes = new long[repeatCount];
        long[] forkedThreadTimes = new long[repeatCount];

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            counts = cwff.countOccurrencesOnSingleThread(folder, args[1]);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , single thread search took " + singleThreadTimes[i] + "ms");
        }

        for(int i = 0;i < repeatCount;i++){
            startTime = System.currentTimeMillis();
            counts = cwff.countOccurrencesInParallel(folder, args[1]);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , fork / join search took " + forkedThreadTimes[i] + "ms");
        }

        System.out.println("\nCSV Output:\n");
        System.out.println("Single thread,Fork/Join");
        for (int i = 0; i < repeatCount; i++) {
            System.out.println(singleThreadTimes[i] + "," + forkedThreadTimes[i]);
        }
        System.out.println();
    }

    String[] wordsIn(String lines){
        return lines.trim().split("(\\s|\\p{Punct})+");
    }

    Long occurrencesCount(Document document, String searchWord){
        long count = 0;
        for(String line : document.getLines()){
            for(String word : wordsIn(line)){
                if(searchWord.equals(word)){
                    count = count + 1;
                }
            }
        }
        return count;
    }

    Long countOccurrencesInParallel(Folder folder, String searchedWord) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, searchedWord));
    }

    static final class Document{

        private final List<String> lines;

        Document(List<String> lines){
            this.lines = lines;
        }

        private List<String> getLines(){
            return this.lines;
        }

        //try块的方式，可以实现java.lang.AutoCloseable的任何类，无论引发何种异常，当执行离开try块时，在此声明的任何资源都将关闭
        static Document fromFile(File file) throws IOException{
            List<String> lines = Lists.newLinkedList();
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line = reader.readLine();
                while(line != null){
                    lines.add(line);
                    line = reader.readLine();
                }
            }
            return new Document(lines);
        }
    }

    static final class Folder{
        private final List<Folder> subFolders;
        private final List<Document> documents;

        Folder(List<Folder> subFolders, List<Document> documents){
            this.subFolders = subFolders;
            this.documents = documents;
        }

        List<Folder> getSubFolders(){
            return this.subFolders;
        }

        List<Document> getDocuments(){
            return this.documents;
        }

        static Folder fromDirectory(File dir) throws IOException{
            List<Document> documents = Lists.newLinkedList();
            List<Folder> subFolders = Lists.newLinkedList();
            for(File entry : dir.listFiles()){
                if(entry.isDirectory()){
                    subFolders.add(Folder.fromDirectory(entry));
                }else{
                    documents.add(Document.fromFile(entry));
                }
            }
            return new Folder(subFolders, documents);
        }
    }

    //计算某个单词在文档中出现的次数
    class DocumentSearchTask extends RecursiveTask<Long>{
        private final Document document;
        private final String searchWord;

        DocumentSearchTask(Document document, String searchWord){
            super();
            this.document = document;
            this.searchWord = searchWord;
        }

        @Override
        protected Long compute(){
            return occurrencesCount(document, searchWord);
        }
    }

    class FolderSearchTask extends RecursiveTask<Long>{

        private final Folder folder;
        private final String searchWord;

        FolderSearchTask(Folder folder, String searchWord){
            super();
            this.folder = folder;
            this.searchWord = searchWord;
        }

        @Override
        protected Long compute(){
            Long count = 0L;
            List<RecursiveTask<Long>> forks = Lists.newLinkedList();
            for(Folder subFolder : folder.getSubFolders()){
                FolderSearchTask task = new FolderSearchTask(subFolder, searchWord);
                forks.add(task);
                task.fork();
            }
            for(Document document : folder.getDocuments()){
                DocumentSearchTask task = new DocumentSearchTask(document, searchWord);
                forks.add(task);
                task.fork();
            }
            for(RecursiveTask<Long> task : forks){
                count = count + task.join();
            }
            return count;
        }
    }

    Long countOccurrencesOnSingleThread(Folder folder, String searchedWord) {
        long count = 0;
        for (Folder subFolder : folder.getSubFolders()) {
            count = count + countOccurrencesOnSingleThread(subFolder, searchedWord);
        }
        for (Document document : folder.getDocuments()) {
            count = count + occurrencesCount(document, searchedWord);
        }
        return count;
    }
}
