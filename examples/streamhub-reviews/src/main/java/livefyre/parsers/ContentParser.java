package livefyre.parsers;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import livefyre.listeners.ContentUpdateListener;
import livefyre.models.Attachments;
import livefyre.models.AuthorsBean;
import livefyre.models.Content;
import livefyre.models.ContentTypeEnum;
import livefyre.models.ReviewStatus;
import livefyre.models.Vote;

public class ContentParser {

    private JSONObject jsonResponseObject;
    private static HashMap<String, AuthorsBean> authorsCollection;
    public static HashMap<String, Content> ContentMap;
    public static List<Content> SortedContents;
    public static String lastEvent = "0";
    static Context mContext;
    ArrayList<Content> parentsList;
    ArrayList<Content> reviewsArray;

    public ContentParser(JSONObject jsonResponseObject, Context mContext) {
        this.jsonResponseObject = jsonResponseObject;
        this.mContext = mContext;
    }

    private ContentUpdateListener l1;
    private static int countFlag = 0;

    public void getContentFromResponse(ContentUpdateListener l1)
            throws JSONException {
        this.l1 = l1;
        ContentMap = new HashMap<>();
        SortedContents = Collections.synchronizedList(new ArrayList<Content>());
        parentsList = new ArrayList<>();
        reviewsArray = new ArrayList<>();
        // Collecting Authors
        JSONObject headDocument = jsonResponseObject
                .getJSONObject("headDocument");
        lastEvent = headDocument.getString("event");

        JSONObject authorsJsonObj = headDocument.getJSONObject("authors");
        authorCollection(authorsJsonObj);
        // Collecting Childes
        JSONArray contentArray = headDocument.getJSONArray("content");
        ArrayList<Content> parsedArray = processContentArray(contentArray);
        addContentsToCollection(parsedArray);
        parentsList = getParentsContentsInOrder(parsedArray);
        reviewsArray = getReviews();
        getChildContents(parsedArray);
        getDeletedObjects();
    }

    public ArrayList<Content> processContentArray(JSONArray contentArray) {
        ArrayList<Content> contentArrayList = new ArrayList<>();
        for (int i = 0; i < contentArray.length(); i++) {
            Content mContent = new Content();

            try {
                JSONObject jsonContent = (JSONObject) contentArray.get(i);
                mContent = processedContentObject(jsonContent, mContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            contentArrayList.add(mContent);
        }
        return contentArrayList;
    }

    private void addContentsToCollection(ArrayList<Content> processedArray) {
        for (Content mContent : processedArray) {
            ContentMap.put(mContent.getId(), mContent);
        }
    }

    Attachments getAttachmentsFromJson(JSONObject jsonAttachment) {
        Attachments mAttachments = new Attachments();
        try {
            if (!jsonAttachment.isNull("url")) {

                mAttachments.setUrl(jsonAttachment.getString("url"));
            }
            if (!jsonAttachment.isNull("link")) {

                mAttachments.setLink(jsonAttachment.getString("link"));
            }
            if (!jsonAttachment.isNull("provider_name")) {

                mAttachments.setProvider_name(jsonAttachment.getString("provider_name"));
            }
            if (!jsonAttachment.isNull("thumbnail_url")) {

                mAttachments.setThumbnail_url(jsonAttachment.getString("thumbnail_url"));
            }
            if (!jsonAttachment.isNull("type")) {

                mAttachments.setType(jsonAttachment.getString("type"));
            }
            if (!jsonAttachment.isNull("html")) {

                mAttachments.setHTML(jsonAttachment.getString("html"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mAttachments;
    }


    public Content processedContentObject(JSONObject contentObject, Content mContent) {
        if (mContent == null)
            mContent = new Content();
        try {
            JSONObject content = contentObject.getJSONObject("content");

            if (!content.isNull("id")) {
                mContent.setId(content.getString("id"));
            }

            if (!content.isNull("authorId")) {
                mContent.setAuthorId(content.getString("authorId"));
                AuthorsBean author = authorsCollection.get(content.getString("authorId"));
                if (author != null)
                    mContent.setAuthor((author));
            }
            if (!content.isNull("parentId")) {
                mContent.setParentId(content.getString("parentId"));

                if (content.getString("parentId").equals(""))
                    mContent.setContentType(ContentTypeEnum.PARENT);
                else
                    mContent.setContentType(ContentTypeEnum.CHILD);
            }
            if (!content.isNull("annotations")) {
                JSONObject annotations = content.getJSONObject("annotations");

                if (!annotations.isNull("moderator")) {
                    mContent.setIsModerator(annotations
                            .getString("moderator"));
                }
                if (!annotations.isNull("rating")) {
                    mContent.setRating(""
                            + (annotations.getJSONArray("rating").get(0)));
                }
                if (!annotations.isNull("isFeatured")) {
                    mContent.setIsFeatured(true);
                }
                if (!annotations.isNull("vote")) {
                    JSONArray vote = annotations.getJSONArray("vote");
                    List<Vote> votes = new ArrayList();

                    for (int j = 0; j < vote.length(); j++) {
                        JSONObject voteJson = vote.getJSONObject(j);
                        Vote v = new Vote();
                        if (!voteJson.isNull("value")
                                && !voteJson.isNull("author")) {
                            v.setValue(voteJson.getString("value"));

                            v.setAuthor(voteJson.getString("author"));
                            votes.add(v);
                        }
                    }
                    mContent.setVote(votes);
                    int count = 0;
                    for (int k = 0; k < votes.size(); k++) {
                        if (votes.get(k).getValue().equals("1"))
                            count++;
                    }
                    mContent.setHelpfulcount(count);
                }
            }
            if (!content.isNull("attachments")) {
                JSONArray attachments = content.getJSONArray("attachments");
                for (int attachmentsCount = 0; attachmentsCount < attachments.length(); attachmentsCount++) {
                    Attachments mAttachment = getAttachmentsFromJson(attachments.getJSONObject(attachmentsCount));
                    mContent.setAttachments(mAttachment);
                }
            }

            if (!content.isNull("createdAt")) {
                mContent.setCreatedAt(content.getString("createdAt"));
            }

            if (!content.isNull("updatedAt")) {
                mContent.setUpdatedAt(content.getString("updatedAt"));
            }

            if (!content.isNull("ancestorId")) {
                mContent.setAncestorId(content.getString("ancestorId"));
            }
            if (!content.isNull("title")) {
                mContent.setTitle(content.getString("title"));
            }
            if (!content.isNull("bodyHtml"))
                mContent.setBodyHtml(content.getString("bodyHtml"));
            else
                mContent.setBodyHtml("");
            if (!contentObject.isNull("vis")) {
                mContent.setVisibility(contentObject.getString("vis"));
                if (mContent.getVisibility().equals("1"))
                    mContent.setReviewStatus(ReviewStatus.NOT_DELETED);
                else
                    mContent.setReviewStatus(ReviewStatus.DELETED);
            }

            if (!contentObject.getString("vis").equals("1"))
                mContent.setContentType(ContentTypeEnum.DELETED);

            if (!contentObject.isNull("rating"))
                mContent.setRating(contentObject.getString("rating"));

            if (!contentObject.isNull("event")) {
                mContent.setEvent(contentObject.getString("event"));
                lastEvent = mContent.getEvent();
            }
            if (!contentObject.isNull("type"))
                mContent.setType(contentObject.getString("type"));
            mContent.setDepth(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mContent;
    }

    void authorCollection(JSONObject authorsJson) {
        if (authorsCollection == null)
            authorsCollection = new HashMap();
        @SuppressWarnings("rawtypes")
        Iterator keys = authorsJson.keys();
        while (keys.hasNext()) {
            String authorId = (String) keys.next();

            AuthorsBean author = new AuthorsBean();
            try {
                if (!authorsJson.getJSONObject(authorId).isNull("avatar"))
                    author.setAvatar(authorsJson.getJSONObject(authorId)
                            .getString("avatar"));
                if (!authorsJson.getJSONObject(authorId).isNull("displayName"))
                    author.setDisplayName(authorsJson.getJSONObject(authorId)
                            .getString("displayName"));
                if (!authorsJson.getJSONObject(authorId).isNull("profileUrl"))
                    author.setProfileUrl(authorsJson.getJSONObject(authorId)
                            .getString("profileUrl"));
                if (!authorsJson.getJSONObject(authorId).isNull("type"))
                    author.setType(authorsJson.getJSONObject(authorId)
                            .getString("type"));
                if (!authorsJson.getJSONObject(authorId).isNull("id"))
                    author.setId(authorsJson.getJSONObject(authorId).getString(
                            "id"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            authorsCollection.put(authorId, author);
        }
    }

    public ArrayList<Content> getReviews(){
        ArrayList<Content> reviewsArray=new ArrayList<>();
        for(Content content :ContentMap.values()){
            if(content.getParentId().equals("") && content.getReviewStatus()==ReviewStatus.NOT_DELETED){
                reviewsArray.add(content);
            }
        }
        return reviewsArray;
    }

    public ArrayList<Content> getParentsContentsInOrder(ArrayList<Content> processedArray) {
        ArrayList<Content> resultedArray = new ArrayList<>();

        for (int i = 0; i < processedArray.size(); i++) {
            Content mContent = processedArray.get(i);
            if (mContent.getParentId().equals("")) {
                resultedArray.add(mContent);
                SortedContents.add(mContent);
                processedArray.remove(mContent);
                i--;
            }
        }
        Collections.sort(resultedArray, new Comparator<Content>() {
            @Override
            public int compare(Content lhs, Content rhs) {
                return Integer.parseInt(rhs.getCreatedAt()) - Integer.parseInt(lhs.getCreatedAt());
            }
        });
        Collections.sort(SortedContents, new Comparator<Content>() {
            @Override
            public int compare(Content lhs, Content rhs) {
                return Integer.parseInt(rhs.getCreatedAt()) - Integer.parseInt(lhs.getCreatedAt());
            }
        });
        return resultedArray;
    }
//    public static List<Content> getChildContentForReview(String contentId) {
//
//        comments = new ArrayList();
//        depth = 0;
//        if (ContentMap.get(contentId) != null) {
//            if (ContentMap.get(contentId).getChildBeanContent() != null)
//                getChildsForReview(ContentMap.get(contentId)
//                        .getChildBeanContent());
//        } else {
//            Log.d("Childs", "No childs");
//        }
//
//        return childs;
//
//    }
//    public void getCommentsforReviews(ArrayList<Content> reviewsList){
//        ArrayList<Content> CommentsArray=new ArrayList<>();
//        for (int position = 0; position < reviewsArray.size(); position++) {
//            Content rContent = reviewsArray.get(position);
//            for (int i = 0; i < reviewsList.size(); i++) {
//                Content reviewContent = reviewsList.get(i);
//                if (rContent.getId().equals(reviewContent.getParentId())) {
//                    CommentsArray.add(reviewContent);
//                }
//            }
//            Collections.sort(CommentsArray, new Comparator<Content>() {
//                @Override
//                public int compare(Content lhs, Content rhs) {
//                    return Integer.parseInt(rhs.getCreatedAt())
//                            - Integer.parseInt(lhs.getCreatedAt());
//                }
//            });
//            for (int i = 0; i < CommentsArray.size(); i++) {
//
//                parentsList.add(position + i + 1, CommentsArray.get(i));
//                SortedContents.add(position + i + 1, CommentsArray.get(i));
//                getChildContents(reviewsList);
//            }
//        }
//    }
    public void getChildContents(ArrayList<Content> mainList) {
        for (int position = 0; position < parentsList.size(); position++) {
            Content mContent = parentsList.get(position);

            ArrayList<Content> mChildContents = new ArrayList<>();
            for (int i = 0; i < mainList.size(); i++) {
                Content mMaiContent = mainList.get(i);
                if (mContent.getId().equals(mMaiContent.getParentId())) {
                    int depthCount = 0;

                    if (mContent.getParentPath() != null) {
                        for (String path : mContent.getParentPath()) {
                            mMaiContent.setParentPath(path);
                            depthCount++;
                        }
                    }

                    mMaiContent.setParentPath(mContent.getId());
                    mContent.setChildPath(mMaiContent.getId());
                    mMaiContent.setDepth(++depthCount);
                    mChildContents.add(mMaiContent);
                    mainList.remove(mMaiContent);
                    i--;
                }
            }

            Collections.sort(mChildContents, new Comparator<Content>() {
                @Override
                public int compare(Content lhs, Content rhs) {
                    return Integer.parseInt(rhs.getCreatedAt())
                            - Integer.parseInt(lhs.getCreatedAt());
                }
            });

            for (int i = 0; i < mChildContents.size(); i++) {

                parentsList.add(position + i + 1, mChildContents.get(i));
                SortedContents.add(position + i + 1, mChildContents.get(i));
                getChildContents(mainList);
            }
        }
    }


    public ArrayList<Content> getDeletedObjects() {
        ArrayList<Content> tempArray = new ArrayList<>(SortedContents);

        for (int i = 0; i < tempArray.size(); i++) {
            Content mContent = tempArray.get(i);
            int flag = 0;
            if (mContent.getChildPath() != null) {
                for (int c = 0; c < mContent.getChildPath().size(); c++) {

                    if (!ContentMap.get(mContent.getChildPath().get(c)).getContentType().equals(ContentTypeEnum.DELETED)) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    tempArray.remove(i);
                    i--;
                }
            } else if (mContent.getContentType().equals(ContentTypeEnum.DELETED)) {
                tempArray.remove(i);
                i--;
            }
        }
        return tempArray;
    }

    public  static ArrayList<Content> getChildForContent(String contentId){
        return getChildContentsRecursive(contentId, new ArrayList<Content>());
    }

    public static ArrayList<Content> getChildContentsRecursive(String contentId, ArrayList<Content> results) {
        Content mContent = ContentMap.get(contentId);
        if (mContent != null) {
            List<String> childPath = mContent.getChildPath();
            if (childPath != null)
                for (int i = 0; i < childPath.size(); i++) {
                    Content childContent = ContentMap.get(childPath.get(i));
                    if (childContent.getVisibility().equals("1")) {
                        results.add(childContent);
//                        return ;
//                    } else {
                    getChildContentsRecursive(childPath.get(i),results);
                    }
                }
        }
        return results;
    }

    public Boolean hasVisibleChildContents(String contentId) {

        Content mContent = ContentMap.get(contentId);
        if (mContent != null) {
            List<String> childPath = mContent.getParentPath();
            if (childPath != null)
                for (int i = 0; i < childPath.size(); i++) {
                    Content childContent = ContentMap.get(childPath.get(i));
                    if (childContent.getVisibility().equals("1")) {
                        return true;
                    } else {
                        hasVisibleChildContents(childPath.get(i));
                    }
                }
        }
        return false;
    }

    public void setStreamData(String data) {

        JSONObject jsonObject;
        HashSet<String> annotationsSet = new HashSet<>();
        HashSet<String> authorsSet = new HashSet<>();
        HashSet<String> statesSet = new HashSet<>();
        HashSet<String> updateSet = new HashSet<>();

        try {
            jsonObject = new JSONObject(data).getJSONObject("data");

            if (!jsonObject.isNull("authors")) {
                JSONObject states = jsonObject.getJSONObject("authors");
                if (states != null) {
                    Iterator keys = states.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        authorsSet.add(key);
                        updateSet.add(key);
                    }
                    authorCollection(states);
                }
            }

            if (!jsonObject.isNull("states")) {
                JSONObject states = jsonObject.getJSONObject("states");
                if (states != null) {
                    Map<String, JSONObject> statesMap = new HashMap<String, JSONObject>();

                    Iterator keys = states.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            statesMap.put(key, states.getJSONObject(key));
                            statesSet.add(key);
                            updateSet.add(key);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                    handleStatesData(statesMap);
                }
            }

            if (!jsonObject.isNull("annotations")) {
                JSONObject annotations = jsonObject
                        .getJSONObject("annotations");
                if (annotations != null) {
                    Map<String, JSONObject> annotationsMap = new HashMap<String, JSONObject>();

                    Iterator keys = annotations.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            annotationsMap.put(key,
                                    annotations.getJSONObject(key));
                            annotationsSet.add(key);
                            updateSet.add(key);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    handleAnnotationsData(annotationsMap);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        l1.onDataUpdate(updateSet);
    }

    private void handleAnnotationsData(Map<String, JSONObject> map) {
        for (String contentId : map.keySet()) {
            JSONObject content = map.get(contentId);
            JSONObject added, removed, updated;
            try {
                added = content.getJSONObject("added");
                removed = content.getJSONObject("removed");
                updated = content.getJSONObject("updated");
                handleAddedAnnotations(contentId, added);
                handleAddedAnnotations(contentId, updated);
                handleRemovedAnnotations(contentId, removed);

                // Get Existed Content from collection of contents
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAddedAnnotations(String contentId, JSONObject added) {
        Content contentbean = ContentMap.get(contentId);
        if (contentbean != null) {
            // Handle Votes in annotation
            if (!added.isNull("vote")) {
                JSONArray vote;
                try {
                    vote = added.getJSONArray("vote");
                    // List<Vote> votes = new ArrayList<>();
                    List<Vote> existedVotes = contentbean.getVote();

                    for (int j = 0; j < vote.length(); j++) {
                        JSONObject voteJson = vote.getJSONObject(j);
                        Vote v = new Vote();
                        if (!voteJson.isNull("value")
                                && !voteJson.isNull("author")) {
                            v.setValue(voteJson.getString("value"));

                            v.setAuthor(voteJson.getString("author"));
                            if (existedVotes != null) {
                                // Log.d("ExistedVotes Votes", "" +
                                // existedVotes);
                                for (int votelocation = 0; votelocation < existedVotes
                                        .size(); votelocation++) {
                                    Vote ev = existedVotes.get(votelocation);
                                    if (ev.getAuthor().equals(
                                            voteJson.getString("author")))
                                        existedVotes.remove(ev);
                                }
                            } else {
                                existedVotes = new ArrayList();
                            }
                            existedVotes.add(v);
                        }
                    }
                    contentbean.setVote(existedVotes);
                    int count = 0;
                    for (int i = 0; i < existedVotes.size(); i++) {
                        if (existedVotes.get(i).getValue().equals("1"))
                            count++;
                    }
                    contentbean.setHelpfulcount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!added.isNull("featuredmessage")) {
                if (contentbean.getIsFeatured() == false) {
                    contentbean.setIsFeatured(true);
                }
            }
        }
    }

    private void handleRemovedAnnotations(String contentId, JSONObject removed) {
        Content contentbean = ContentMap.get(contentId);
        if (contentbean != null) {
            // Handle Votes in annotation
            if (!removed.isNull("vote")) {
                JSONArray vote;
                try {
                    vote = removed.getJSONArray("vote");
                    List<Vote> existedVotes = contentbean.getVote();

                    for (int j = 0; j < vote.length(); j++) {
                        JSONObject voteJson = vote.getJSONObject(j);
                        Vote v = new Vote();
                        if (!voteJson.isNull("value")
                                && !voteJson.isNull("author")) {
                            v.setValue(voteJson.getString("value"));

                            v.setAuthor(voteJson.getString("author"));
                            if (existedVotes != null) {
                                for (int position = 0; position < existedVotes
                                        .size(); position++) {
                                    Vote ev = existedVotes.get(position);
                                    if (ev.getAuthor().equals(
                                            voteJson.getString("author")))
                                        existedVotes.remove(ev);
                                }
                            } else {
                                existedVotes = new ArrayList();
                            }
                        }
                    }
                    contentbean.setVote(existedVotes);
                    int count = 0;
                    for (int i = 0; i < existedVotes.size(); i++) {
                        if (existedVotes.get(i).getValue().equals("1"))
                            count++;
                    }

                    contentbean.setHelpfulcount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!removed.isNull("featuredmessage")) {
                if (contentbean.getIsFeatured() == true) {
                    contentbean.setIsFeatured(false);
                }
            }
        }
    }

    private void handleStatesData(Map<String, JSONObject> map) {
        for (JSONObject mainContent : map.values()) {
            try {
                JSONObject content = mainContent.getJSONObject("content");
                String parentId = null;
                if (!content.isNull("parentId"))
                    parentId = content.getString("parentId");
                Content parentBean = ContentMap.get(parentId);
                Content newBean = ContentMap.get(content.getString("id"));
                boolean isEdit = false;
                if (newBean == null) {
                    newBean = new Content();
                } else {
                    isEdit = true;
                }
                if (mainContent.getString("vis").equals("1")) {
                    // New or Edited
                    if (parentBean != null) {
                        newBean = processedContentObject(mainContent, newBean);

                        if (!isEdit) {
                            if (parentBean.getParentPath() != null)
                                for (String parentPathID : parentBean.getParentPath()) {
                                    newBean.setParentPath(parentPathID);
                                }
                            newBean.setParentPath(parentBean.getId());

                        }


                        Log.d("Parents", "" + newBean.getParentPath());
                        List<String> list;
                        if (parentBean.getChildPath() != null) {
                            list = parentBean.getChildPath();
                            Boolean flag = false;
                            for (String listID : list) {
                                if (listID.equals(newBean.getId()))
                                    flag = true;
                            }
                            if (!flag)
                                list.add(newBean.getId());
                        } else {
                            list = new ArrayList();
                            list.add(newBean.getId());
//                            newBean.setChildPath(newBean.getId());
                        }
                    } else {
                        newBean = processedContentObject(mainContent, newBean);
                    }
                    ContentMap.put(newBean.getId(), newBean);
                } else {
                    // Deleted
                    String deletedParent = newBean.getParentId();
                    parentBean = ContentMap.get(deletedParent);
                    if (parentBean != null) {
                        Log.d("Depth ", "Parent Depth" + parentBean.getDepth());
                        newBean = processedContentObject(mainContent, newBean);
                        newBean.setParentPath(parentBean.getId());
                    } else {
                        // Parent Deleted
                        newBean = processedContentObject(mainContent, newBean);
                    }
                    ContentMap.put(newBean.getId(), newBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}