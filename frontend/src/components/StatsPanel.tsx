type StatsPanelProps = { label: string; value: number | string;}

function StatsPanel({ label, value }: StatsPanelProps) {
    return (
        <div className="flex items-center justify-between rounded p-4">
            <span className="text-lg font-bold">{label}</span>
            <span className="text-2xl font-bold">{value}</span>
        </div>
    )
}

export default function StatsPanel(props: {stats?: any})
 const s = props.stats ?? {documents: 0, types: 0, tags: 0, comments: 0};
    return (
        <aside className="bg-white border rounded p-4">
            <div className="font-medium mb-2">Statistics</div>
            <div className="space-y-1">
                <StatsPanel label="Documents" value={s.documents} />
                <StatsPanel label="Types" value={s.types} />
                <StatsPanel label="Tags" value={s.tags} />
                <StatsPanel label="Unscanned" value={s.unscanned} />
                <StatsPanel label="Characters" value={s.characters} />
                <StatsPanel label="Trees Saved" value={s.treesSaved} />
                <StatsPanel label="Comments" value={s.comments} />
            </div>
        </aside>

    )