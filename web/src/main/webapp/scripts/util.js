/*$(document).ready(function ()
 {
 var floatingItem = $('.floating');
 if (floatingItem == null || floatingItem.offset() == null) return;
 var top = floatingItem.offset().top - parseFloat(floatingItem.css('marginTop').replace(/auto/, 0))
 - $('.menuBar').height();
 $(window).scroll(function ()
 {
 var y = $(this).scrollTop();
 if (y >= top) {
 floatingItem.addClass('fixed');
 } else {
 floatingItem.removeClass('fixed');
 }
 });
 });
 */
function valid(el) {
    return $(el).valid();
}
function excludeAddedTag(el, inputSelector) {
    var parents = $(el).parents('.added-tag');
    parents.fadeOut(300, function() {
        parents.remove();
        $(inputSelector).removeAttr('disabled')
    });
}
function addSelectedTag(inputSelector, wrapper, tag) {
    if (wrapper.find('[name=selectedTag][value='+tag+']').length == 0) {
        var newTag = $('<div class="tag added-tag" style="display: none">' +
            ' <div class="tag-ref-inactive">' +
            '  <div class="tag-label">' + tag + "</div>" +
            '  <div class="remove-added-tag" title="удалить" onclick="' + createRemoveTagHandler(inputSelector) + '">×</div>' +
            ' </div>' +
            '<input type="hidden" name="selectedTag" value="' + tag + '"/>' +
            '</div>');
        wrapper.append(newTag);
        newTag.fadeIn(300);
    }
    $(inputSelector).val("");
}

function createRemoveTagHandler(inputSelector) {
    return 'excludeAddedTag(this, \''+inputSelector+'\');';
}

function checkTagsInputDisabled(wrapper, input, maxTags) {
    if (wrapper.find('[name=selectedTag]').length >= maxTags) {
        input.attr('disabled', 'disabled');
    } else {
        wrapper.removeAttr('disabled');
    }
}

function initTagsCompletion(selector, wrapperSelector, maxTags, url) {
    $(document).ready(function () {
            var wrapper = $(wrapperSelector);
            var input = $(selector);
            checkTagsInputDisabled(wrapper, input, maxTags);
            input.autocomplete({
                    source:url,
                    select:function (event, ui) {
                        addSelectedTag(selector, wrapper, ui.item.value);
                        checkTagsInputDisabled(wrapper, input, maxTags);
                        return false;
                    }
                }
            ).data("autocomplete")._renderItem = function (ul, item) {
                var innerHtml = "<a>"  + item.label;
                if(item.desc) {
                    innerHtml = innerHtml + "<br>" + item.desc;
                }
                innerHtml = innerHtml + "</a>";
                return $("<li></li>")
                    .data("item.autocomplete", item)
                    .append(innerHtml)
                    .appendTo(ul);
            };

            wrapper.find(".remove-added-tag").attr("onclick", createRemoveTagHandler(selector));
        }
    );
}

function hideNewCommentInput(id) {
    var input = $('#'+id).children('.new-comment-input');
    input.attr('rows', 1);
    input.val('');
    input.height('auto');
    $('#'+id).children('.new-comment-buttons-wrapper').hide();
}
function showNewCommentInput(id) {
    var input = $('#'+id).children('.new-comment-input');
    input.attr('rows', 2);
    $('#'+id).children('.new-comment-buttons-wrapper').show();
}
function checkNewCommentButtonsEnabled(input, id) {
    if (input != null) {
        id = $(input).parent('.new-comment-input-wrapper').attr('id');
        if (id) {
            window.setTimeout(function(){
                checkNewCommentButtonsEnabled(null, id);
            }, 300);
        }
    } else {
        var inputText = $('#'+id).children('.new-comment-input')[0].value;
        if (inputText == null || inputText.length == 0) {
            hideNewCommentInput(id);
        } else {
            showNewCommentInput(id);
        }
    }
}

function rewriteUrl(stateType, state, title, urlPath){
    document.title = title;
    window.history.replaceState({"pageTitle":title, "state":state, "stateType":stateType}, title, urlPath);
}
function addHistory(stateType, state, title, urlPath){
    document.title = title;
    window.history.pushState({"pageTitle":title, "state":state, "stateType":stateType}, title, urlPath);
}
function initBackForward(requestUrl, stateType, state, title, urlPath) {
    window.onpopstate = function (e) {
        if (e.state) {
//            alert(e.state.state);
//            alert(window.history.state.state);
            document.title = e.state.pageTitle;
            $.ajax({
                type:"GET",
                url:requestUrl,
                data:{ newStateType:e.state.stateType, newState:e.state.state }
            }).done(function (msg) {
                    //alert("Data Saved: " + msg);
                });
        } else {
            //rewriteUrl(stateType, state, title, urlPath);
        }
    };
}
