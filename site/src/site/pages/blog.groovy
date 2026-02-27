import generator.DocUtils
import org.asciidoctor.ast.Document

modelTypes = {
    Document doc
    String title
    String notes
    Map<String, String> related
}

title = doc.structuredDoctitle.combined
def metas = [:]
if (doc.attributes.keywords) {
    metas.keywords = doc.attributes.keywords
}
if (doc.attributes.description) {
    metas.description = doc.attributes.description
}

layout 'layouts/main.groovy', true,
        pageTitle: "The Apache Groovy programming language - Blogs - $title",
        extraStyles: [relative('css/prettify.min.css')],
        extraMeta: metas,
        extraFooter: contents {
            script(src:relative('js/vendor/prettify.min.js')) { }
            script { yieldUnescaped "document.addEventListener('DOMContentLoaded',prettyPrint)" }
        },
        mainContent: contents {
            Map options = [attributes:[DOCS_BASEURL:DocUtils.DOCS_BASEURL]]
            def notesAsHTML = asciidocText(notes,options)
            def matcher = notesAsHTML =~ /<h2 id="(.+?)">(.+?)<\/h2>/
            def sections = [:]
            while (matcher.find()) {
                sections[matcher.group(1)] = matcher.group(2)
            }

            div(id: 'content', class: 'page-1') {
                div(class: 'row') {
                    div(class: 'row-fluid') {
                        div(class: 'col-lg-3') {
                            ul(class: 'nav-sidebar') {
                                li {
                                    a(href: './', 'Blog index')
                                }
                                li(class:'active') {
                                    a(href: '#doc', title)
                                }
                                sections.each { k,v ->
                                    li {
                                        a(href:"#$k", class: 'anchor-link', v)
                                    }
                                }
                            }
                            if (related) {
                                br()
                                ul(class: 'nav-sidebar') {
                                    li(style: 'padding: 0.35em 0.625em; background-color: #eee') {
                                        span('Related posts')
                                    }
                                    related.each { bn, title ->
                                        li {
                                            a(href:"./$bn", title)
                                        }
                                    }
                                }
                            }
                        }

                        div(class: 'col-lg-8 col-lg-pull-0') {
                            a(name:"doc"){}
                            h1(title)
                            p {
                                if (doc.authors) {
                                    div(style: "display:flex;padding:0.2ex") {
                                        def multiple = doc.authors.size() > 1
                                        span("Author${multiple ? 's' : ''}:&nbsp;")
                                        yieldUnescaped doc.authors.collect { DocUtils.prettyAuthors(it, this.&relative) }.join('<span style="width:2ex"></span>')
                                    }
                                }
                                if (doc.revisionInfo?.date) {
                                    br()
                                    def publishDate = DocUtils.prettyDate(doc.revisionInfo.date)
                                    def updateDate = doc.attributes.updated ? DocUtils.prettyDate(doc.attributes.updated?.toString()) : null
                                    span("Published: $publishDate${updateDate ? / (Last updated: $updateDate)/ : ''}")
                                }
                            }
                            hr()
                            yieldUnescaped notesAsHTML
                        }
                    }
                }
            }
        }
