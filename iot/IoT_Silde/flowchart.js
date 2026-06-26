mermaid.initialize({
    startOnLoad: true,
    theme: 'default',
    flowchart: {
        useMaxWidth: true,
        htmlLabels: true,
        curve: 'basis'
    }
});

function goBack() {
    window.close();
}

function exportDiagram() {
    alert('Export feature');
}

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') goBack();
});
