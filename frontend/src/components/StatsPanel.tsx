// placement: H - Right Stats Panel

type StatsRowProps = { label: string; value: number | string };

function StatsRow({ label, value }: StatsRowProps) {
  return (
    <div className="flex items-center justify-between rounded p-4">
      <span className="text-medium font-bold">{label}</span>
      <span className="inline-flex items-center px-3 py-1 rounded-full bg-btn2 text-white text-sm font-semibold whitespace-nowrap tabular-nums">
        {value}
      </span>
    </div>
  );
}

export default function StatsPanel(props: { stats?: any }) {
  const s =
    props.stats ?? ({ savedTrees: 168, documents: 142, characters: 82344, comments: 15, types: 12, tags: 14 } as const);

  return (
    <aside className="bg-bg border rounded p-4">
      <div className="text-2xl font-extrabold mb-2">Statistics</div>
      <div className="space-y-0">
        <StatsRow label="Trees Saved" value={s.savedTrees} />
        <StatsRow label="Documents" value={s.documents} />
        <StatsRow label="Characters" value={s.characters} />
        <StatsRow label="Comments" value={s.comments} />
        <StatsRow label="Types" value={s.types} />
        <StatsRow label="Tags" value={s.tags} />
      </div>
    </aside>
  );
}