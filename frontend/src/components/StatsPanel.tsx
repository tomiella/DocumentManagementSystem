type StatsPanelProps = { label: string; value: number | string;}

function Stats({ label, value }: StatsPanelProps) {
    return (
        <div className="flex items-center justify-between rounded p-4">
            <span className="text-lg font-bold">{label}</span>
            <span className="text-2xl font-bold">{value}</span>
        </div>
    )
}

export default function StatsPanel(props: { stats?: any }) {
    const s = props.stats ?? { documents: 142, types: 12, tags: 14, unscanned: 15, characters: 82344, savedTrees: 168 };
    return (
        <aside className="bg-white border rounded p-4">
            <div className="font-medium mb-2">Statistics</div>
            <div className="space-y-1">
                <Stats label="Documents" value={s.documents} />
                <Stats label="Types" value={s.types} />
                <Stats label="Tags" value={s.tags} />
                <Stats label="Characters" value={s.characters} />
                <Stats label="Comments" value={s.comments} />
                <Stats label="Trees Saved" value={s.treesSaved} />
            </div>
        </aside>
    )
}
